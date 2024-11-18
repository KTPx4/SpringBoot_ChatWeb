package com.Px4.ChatAPI.controllers.socket.ChatController;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.controllers.requestParams.relation.ResponseFriends;
import com.Px4.ChatAPI.controllers.requestParams.relation.ResponseGroupChat;
import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.Px4Response;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.relation.FriendDetail;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    @Autowired
    ChatService chatService;
    @GetMapping
    public ResponseEntity<Px4Response> getALl()
    {
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        ResponseFriends res = null;
        try{

           res = chatService.getAllChat(true);

        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            if(e.getMessage().startsWith("friend"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new Px4Response<>(mess, res), status);
    }
    @GetMapping("/friend/{id}")
    public ResponseEntity<Px4Response> getFriendChat(@PathVariable String id, @RequestParam(value = "page" , defaultValue = "1") int page)
    {
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        List<MessageModel> listMess = null;
        try{
            listMess =  chatService.getFriendChat(id , page);
        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e!=null && e.getMessage().startsWith("chat"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new Px4Response<>(mess, listMess), status);
    }
    @GetMapping("/group")
    public ResponseEntity<Px4Response> getAllGroupChat()
    {
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        ResponseGroupChat res = null;
        try{
            res = chatService.getAllChatGroup();

        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e!=null && e.getMessage().startsWith("chat"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new Px4Response<>(mess, res), status);
    }

    @GetMapping("/group/{group}")
    public ResponseEntity<Px4Response> getGroupChat(@PathVariable String group, @RequestParam(value = "page" , defaultValue = "1") int page)
    {
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        List<MessageModel> listMess = null;
        try{
            listMess =  chatService.getConservation(group , page);

        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e!=null && e.getMessage().startsWith("chat"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new Px4Response<>(mess, listMess), status);
    }

}
