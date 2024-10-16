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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

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
        return (username) -> {
            AccountModel account = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

            return User.builder()
                    .username(account.getUsername())
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

                .csrf(s->s.disable())
                .authorizeHttpRequests(auth ->
//                        auth
//                        .requestMatchers("/api/v1/account/login").permitAll()
//                        .requestMatchers("/api/v1/account/register").permitAll()
//                        .requestMatchers("/ws/**").permitAll()
//                        .anyRequest().authenticated()
                    {
                        try{
                            IgnoreRequest.getIgnoreList().forEach(ignore -> auth.requestMatchers(ignore).permitAll());
                            auth.anyRequest().authenticated();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                )
                .exceptionHandling(excH -> excH.accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Thêm bộ lọc JWT trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
