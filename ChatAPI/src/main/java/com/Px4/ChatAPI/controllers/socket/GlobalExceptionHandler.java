package com.Px4.ChatAPI.controllers.socket;
import com.Px4.ChatAPI.models.Px4Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    public GlobalExceptionHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public void handleJwtAuthenticationException(JwtAuthenticationException ex) {
        // Gửi thông báo lỗi đến một topic cụ thể mà client đang lắng nghe
        messagingTemplate.convertAndSend("/topic/errors", new Px4Response("Could not establish connection", ex.getMessage()));
    }
}