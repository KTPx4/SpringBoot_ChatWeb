package com.Px4.ChatAPI.controllers.socket;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.message.MessageRequest;
import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.models.message.MessageResponse;
import com.Px4.ChatAPI.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

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

    @MessageMapping("/connect")
    public void getConnect(@Header("simpUser") Principal principal, MessageRequest message)
    {

    }

    // Mapping đến endpoint /app/chat để xử lý tin nhắn chat
    @MessageMapping("/chat")
//    @SendTo("/topic/messages")
    public void sendMessage(@Header("simpUser") Principal principal, MessageRequest message) {
        // Lấy username từ token để gán cho tin nhắn
        String userID = principal.getName();

        String groupId = message.getTo(); // this is id of groupd

        MessageResponse messResponse = new MessageResponse();
        messResponse.setTo(groupId);


        // loop group and send each members
        try{
            List<String> members = chatService.canSendMess(userID, groupId);
            if(members.size() > 0)
            {
                // create message model

                messResponse.setType("chat");
                messResponse.setSender(userID);
                messResponse.setContent(message.getContent());
                messResponse.setContentType(message.getContentType());
                if(message.getReplyMessageId() != null ) messResponse.setReplyMessageId(message.getReplyMessageId());

                String contentType = message.getContentType();

                MessageModel messDB =  chatService.createMessage(groupId, userID, message.getContentType(), message.getContent(), message.getReplyMessageId()); // create from db

                messResponse.setId(messDB.getId()); // set id of message model

                members.forEach(id -> { // send notice to each member
                    switch (contentType)
                    {
                        case "text":

                            messagingTemplate.convertAndSendToUser(id, "/topic/messages", messResponse);
                            break;

                        case "file":
                            break;

                        case "json":
                            break;
                    }
                });

            }
        }
        catch (Exception e)
        {
            if(e.getMessage().startsWith("conversation"))
            {
                String err = e.getMessage().split("-")[1];

                messResponse.setType("error");
                messResponse.setSender("server");
                messResponse.setContent(err);
                messResponse.setContentType("text");

                messagingTemplate.convertAndSendToUser(userID, "/topic/messages", messResponse);
            }
        }




    }

    @MessageMapping("/get")
    public void getMessage(@Header("simpUser") Principal principal, MessageRequest message)
    {
        // field to is group
    }


}
