package com.Px4.ChatAPI.controllers.JWT;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${spring.datasource.jwtsecret_key}")
    private String SECRET_KEY; // Thay thế bằng secret key của bạn

    public String extractUsername(String token) throws Exception{
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) throws Exception{
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception{
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws Exception{
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())  // Đảm bảo mã hóa đúng dạng byte
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }
        catch (Exception e)
        {
            throw new Exception("Invalid Token");
        }


    }

    private Boolean isTokenExpired(String token) throws Exception{
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token valid for 10 hours
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) throws Exception{
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}