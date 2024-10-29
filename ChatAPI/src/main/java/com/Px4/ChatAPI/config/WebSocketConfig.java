package com.Px4.ChatAPI.config;

import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.controllers.socket.JwtAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.stomp.StompCommand;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;



    private final UserDetailsService userDetailsService;



    @Autowired
    public WebSocketConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;

    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/list");
        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();

        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =  MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                try {
                    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                        String token = accessor.getFirstNativeHeader("Authorization");

                        if (token == null || !token.startsWith("Bearer ")) {
                            // Gắn cờ lỗi vào header
                            throw new JwtAuthenticationException("miss value");

                        }

                        token = token.substring(7);
                        String id = jwtUtil.extractID(token);

                        if (id != null) {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(id);

                            if (jwtUtil.validateToken(token, userDetails)) {
                                UsernamePasswordAuthenticationToken auth =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(auth);
                                accessor.setUser(auth);
                                accessor.getSessionAttributes().put("id", id);
                                return message;
                            }
                        }

                        // Token không hợp lệ
                        accessor.setNativeHeader("error", "Invalid Authorization token");
                        return null;
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    throw new JwtAuthenticationException(e.getMessage());

                }

                return message;
            }
        });
    }




}
