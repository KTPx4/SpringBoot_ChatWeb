package com.Px4.ChatAPI.config;

import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.models.message.MessageChat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.stomp.StompCommand;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private UserDetailsService userDetailsService;

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
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
                try{
                    assert accessor != null;
                        // Lấy token từ header "Authorization"
                    System.out.println("Headers: {}" + accessor);

                    assert accessor != null;
                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                        String token = accessor.getFirstNativeHeader("Authorization");
                        System.out.println("ok:" + token);


                        if (token != null && token.startsWith("Bearer ")) {
                            token = token.substring(7);
                            String id = jwtUtil.extractID(token);
                            System.out.println("id:" + id);

                            if (id != null) {
                                UserDetails userDetails = userDetailsService.loadUserByUsername(id);
                                boolean valid = false;
                                valid = jwtUtil.validateToken(token, userDetails);

                                if (valid) {
                                    System.out.println("ok valid:" + token);

                                    UsernamePasswordAuthenticationToken auth =
                                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                    SecurityContextHolder.getContext().setAuthentication(auth);
                                    accessor.setUser(auth);
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    sendErrorMessage(accessor.getSessionId(), e.getMessage());
                    return null;
                }

                return message;
            }
        });
    }

    private void sendErrorMessage(String sessionId, String errorMessage) {
        MessageChat errorMessageObj = new MessageChat("error", errorMessage, "server");
        String jsonErrorMessage;
        try {
            jsonErrorMessage = objectMapper.writeValueAsString(errorMessageObj);
            for (WebSocketSession session : sessions) {
                if (session.getId().equals(sessionId)) {
                    session.sendMessage(new TextMessage(jsonErrorMessage));
                    session.close();  // Close the connection
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
