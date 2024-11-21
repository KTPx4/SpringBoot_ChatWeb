package com.Px4.ChatAPI.controllers.socket;

import com.Px4.ChatAPI.controllers.requestParams.chat.SeenRequest;
import com.Px4.ChatAPI.controllers.requestParams.relation.FriendItem;
import com.Px4.ChatAPI.controllers.requestParams.relation.GroupChatItem;
import com.Px4.ChatAPI.controllers.requestParams.relation.UpdateGroup;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.controllers.requestParams.chat.MessageRequest;
import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.requestParams.chat.MessageResponse;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.services.GroupService;
import com.Px4.ChatAPI.services.RelationService;
import com.Px4.ChatAPI.services.chat.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final RelationService relationService;
    private final GroupService groupService;

    @Autowired
    public SocketController(SimpMessagingTemplate messagingTemplate, JwtRequestFilter jwtRequestFilter, ChatService chatService, RelationService relationService, GroupService groupService) {
        this.messagingTemplate = messagingTemplate;
        this.jwtRequestFilter = jwtRequestFilter;
        this.chatService = chatService;
        this.relationService = relationService;
        this.groupService = groupService;
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
            GroupModel gr = chatService.canSendMess(userID, groupId);
            if(gr.getMembers().size() > 0)
            {
                // create message model

                messResponse.setType("chat");
                messResponse.setSender(userID);
                messResponse.setContent(message.getContent());
                messResponse.setContentType(message.getContentType());
                if(message.getReplyMessageId() != null ) messResponse.setReplyMessageId(message.getReplyMessageId());

                String contentType = message.getContentType();

                MessageModel messDB =  chatService.createMessage(gr.getId(), userID, message.getContentType(), message.getContent(), message.getReplyMessageId()); // create from db

                messResponse.setId(messDB.getId()); // set id of message model
                messResponse.setCreatedAt(messDB.getCreatedAt());
                messResponse.setAvatar(messDB.getAvatar());
                messResponse.setSenderName(messDB.getSenderName());
                gr.getMembers().forEach(id -> { // send notice to each member
                    switch (contentType)
                    {
                        case "text":

                            messagingTemplate.convertAndSendToUser(id, "/topic/messages", messResponse);
                            break;

                        case "image":
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

    @MessageMapping("/update.group")
    public void updateGroup(@Header("simpUser") Principal principal, UpdateGroup updateGroup)
    {
        // Lấy username từ token để gán cho tin nhắn
        String userID = principal.getName();
        try{
            String idGroup = updateGroup.getId();
            if(idGroup == null || idGroup.isEmpty()) throw  new Exception("conversation-Id group must not null");
            GroupModel gr = chatService.canSendMess(userID, idGroup);
            GroupChatItem grI = groupService.update(idGroup, updateGroup, "");

            if(gr.getMembers().size() > 0)
            {


                gr.getMembers().forEach(id -> { // send notice to each member
                    messagingTemplate.convertAndSendToUser(id, "/update/group", grI);
                });
            }


        }
        catch (Exception e)
        {
            if(e.getMessage().startsWith("conversation"))
            {
                String err = e.getMessage().split("-")[1];
                MessageResponse messResponse = new MessageResponse();
                messResponse.setType("error");
                messResponse.setSender("server");
                messResponse.setContent(err);
                messResponse.setContentType("text");

                messagingTemplate.convertAndSendToUser(userID, "/update/group", messResponse);
            }
            else{
                e.printStackTrace();
            }
        }
    }
    @MessageMapping("/seen")
    public void setSeen(@Header("simpUser") Principal principal, SeenRequest seenRequest) {
        String groupId = seenRequest.getTo();

        // Lấy username từ token để gán cho tin nhắn
        String userID = principal.getName();



        MessageResponse messResponse = new MessageResponse();
        messResponse.setTo(groupId);


        // loop group and send each members
        try{
            GroupModel gr = chatService.canSendMess(userID, groupId);
            if(gr.getMembers().size() > 0)
            {
                // create message model

                messResponse.setType("seen");
                messResponse.setSender(userID);
                messResponse.setTo(groupId);
                messResponse.setContent("");
                messResponse.setContentType("text");
                messResponse.setReplyMessageId("");



                chatService.setSeen(gr.getId(), userID);

                messResponse.setId(""); // set id of message model

                gr.getMembers().forEach(id -> { // send notice to each member
                    messagingTemplate.convertAndSendToUser(id, "/topic/messages", messResponse);
                });

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
    @MessageMapping("/friend")
    public void addFriend(@Header("simpUser") Principal principal, SeenRequest seenRequest)
    {
        String friendId = seenRequest.getTo();

        // Lấy username từ token để gán cho tin nhắn
        String userID = principal.getName();



        MessageResponse messResponse = new MessageResponse();
        messResponse.setTo(friendId);


        // loop group and send each members
        try{
            FriendItem friendItem =  relationService.addFriend(userID, friendId);
            //GroupModel gr = chatService.canSendMess(userID, groupId);
            messResponse.setType("friend");
            messResponse.setSender(userID);
            messResponse.setContent(friendItem.toJson());
            messResponse.setContentType("text");
            messResponse.setReplyMessageId("");

            messResponse.setId(""); // set id of message model

            messagingTemplate.convertAndSendToUser(userID, "/topic/messages", messResponse);

        }
        catch (Exception e)
        {
            e.printStackTrace();
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
    @MessageMapping("/friend.search")
    public void searchFriend(@Header("simpUser") Principal principal, SeenRequest seenRequest)
    {
        String searchName = seenRequest.getTo();

        // Lấy username từ token để gán cho tin nhắn
        String userID = principal.getName();



        MessageResponse messResponse = new MessageResponse();
        messResponse.setTo("");


        // loop group and send each members
        try{
            List<FriendItem> friendItem =  relationService.searchByName(userID, searchName);

            //GroupModel gr = chatService.canSendMess(userID, groupId);
            messResponse.setType("search");
            messResponse.setSender(userID);
            messResponse.setContent(convertListToJson(friendItem));
            messResponse.setContentType("text");
            messResponse.setReplyMessageId("");

            messResponse.setId(""); // set id of message model

            messagingTemplate.convertAndSendToUser(userID, "/topic/messages", messResponse);

        }
        catch (Exception e)
        {
            e.printStackTrace();
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

    // Chuyển đổi List<FriendItem> thành JSON
    public String convertListToJson(List<FriendItem> friendItemList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Sử dụng ObjectMapper để chuyển đổi list thành chuỗi JSON
            return objectMapper.writeValueAsString(friendItemList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi xảy ra
        }
    }

}
