package com.Px4.ChatAPI.controllers.JWT;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public  class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try{
            String requestPath = request.getRequestURI();

            // Bỏ qua các endpoint không yêu cầu xác thực
            if (requestPath.equals("/api/account/login") || requestPath.equals("/api/account/register")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authorizationHeader = request.getHeader("Authorization");

            String username = null;
            String jwt = null;

            // Kiểm tra xem Authorization header có chứa Bearer token hay không
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new Exception("Please Login");
            }

            jwt = authorizationHeader.substring(7);  // Bỏ chữ "Bearer " để lấy token
            username = jwtUtil.extractUsername(jwt); // Trích xuất username từ token

            // Xác thực người dùng nếu có token và người dùng chưa được xác thực trong SecurityContextHolder
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Xác thực token
                if(!jwtUtil.validateToken(jwt, userDetails))
                {
                    throw new Exception("Invalid User or Login Expired");
                }


                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails( request));

                // Thiết lập đối tượng Authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

            // Tiếp tục chuỗi xử lý filter
            filterChain.doFilter(request, response);

        }
        catch (Exception e)
        {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(e.getMessage());
            //throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

    }


}