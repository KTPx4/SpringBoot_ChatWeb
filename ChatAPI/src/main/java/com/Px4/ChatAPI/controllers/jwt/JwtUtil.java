package com.Px4.ChatAPI.controllers.jwt;


import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.models.jwt.BlackListModel;
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

    public String extractID(String token) throws Exception{
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) throws Exception{
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception{
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String id) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, id);
    }

    public Boolean validateToken(String token, UserDetails userDetails) throws Exception{
        final String id = extractID(token);
        return (id.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
            throw new Exception(ResponeMessage.jwtInvalid);
        }


    }

    private Boolean isTokenExpired(String token) throws Exception{
        return extractExpiration(token).before(new Date());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * BlackListModel.TIME_LIVE)) // Token valid for 7 day
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }


}