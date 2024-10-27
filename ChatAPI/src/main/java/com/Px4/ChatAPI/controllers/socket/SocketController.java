package com.Px4.ChatAPI.controllers.socket;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.models.message.MessageChat;
import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class SocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final JwtRequestFilter jwtRequestFilter;
    private final ChatService chatService;
    @Autowired
    public SocketController(SimpMessagingTemplate messagingTemplate, JwtRequestFilter jwtRequestFilter, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.jwtRequestFilter = jwtRequestFilter;
        this.chatService = chatService;
    }

    // Mapping đến endpoint /app/chat để xử lý tin nhắn chat
    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
    public void sendMessage(@Header("simpUser") Principal principal, MessageChat message) {
        // Lấy username từ token để gán cho tin nhắn
        String userID = principal.getName();

        System.out.println("message get from: " + userID);

        String sendToID = message.getTo();

        if(sendToID.toLowerCase().equals("server"))
        {
            message.setContent(userID + " đã kết nối");
            messagingTemplate.convertAndSend("/topic/messages", message);
        }
        else if(chatService.canSendMess(userID, sendToID))
        {
            String typeChat = message.getType().toLowerCase();
            message.setSender(userID);
            switch (typeChat)
            {
                case "text":
                    messagingTemplate.convertAndSendToUser(sendToID, "/topic/messages", message);
                    break;

                case "image":
                    break;

                case "icon":
                    break;
            }
        }
        else{
            message.setType("error");
            message.setSender("server");
            message.setSender(ResponeMessage.ErrorSendMessage);
            messagingTemplate.convertAndSendToUser(userID, "/topic/messages", message);
        }

    }


}
