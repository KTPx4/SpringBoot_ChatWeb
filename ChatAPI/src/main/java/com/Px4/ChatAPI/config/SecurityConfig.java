package com.Px4.ChatAPI.config;


import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig   {

    // private final TestAccount testAccount = new TestAccount();
    private final AccountRepository accountRepository;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;



    public SecurityConfig(AccountRepository accountRepository, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.accountRepository = accountRepository;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    // Bean cho UserDetailsService để Spring sử dụng
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return (id) -> {

            AccountModel account = accountRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!!"));

            return User.builder()
                    .username(account.getId())
                    .password(passwordEncoder.encode(account.getPassword()))
                    .roles(account.getRole())
                    .build();
        };
    }

    // Bean cho JwtRequestFilter, sử dụng JwtUtil và UserDetailsService
    @Bean
    public JwtRequestFilter jwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        return new JwtRequestFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(crs -> crs.configurationSource(corsConfigurationSource())) // Enable CORS with custom configuration
                .authorizeHttpRequests(auth -> {
                    try {
                        IgnoreRequest.getIgnoreList().forEach(ignore -> auth.requestMatchers(ignore).permitAll());
                        // Các route bắt đầu bằng `/api` yêu cầu xác thực
//                        auth.requestMatchers("/api/**").authenticated();
                        // Các request còn lại không yêu cầu xác thực
                        auth.anyRequest().authenticated(); // Đây là rule cần thiết để không gây lỗi
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .exceptionHandling(excH -> excH.accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Thêm bộ lọc JWT trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
