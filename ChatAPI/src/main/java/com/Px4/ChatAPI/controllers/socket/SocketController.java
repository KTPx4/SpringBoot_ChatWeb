package com.Px4.ChatAPI.controllers.socket;

import com.Px4.ChatAPI.models.message.MessageChat;
import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SocketController(SimpMessagingTemplate messagingTemplate, JwtRequestFilter jwtRequestFilter) {
        this.messagingTemplate = messagingTemplate;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    // Mapping đến endpoint /app/chat để xử lý tin nhắn chat
    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
    public void sendMessage(MessageChat message) {
        // Lấy username từ token để gán cho tin nhắn
        System.out.println("message get: " + message.toString());

        messagingTemplate.convertAndSend("/topic/messages", message);
        //return message; // Hoặc xử lý khi không xác thực được
    }


}
