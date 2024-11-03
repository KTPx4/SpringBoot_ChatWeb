package com.Px4.ChatAPI.controllers.socket.ChatController;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.models.Px4Response;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.relation.FriendDetail;
import com.Px4.ChatAPI.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    @Autowired
    ChatService chatService;
    @GetMapping
    public ResponseEntity<Px4Response> getAllChat(@RequestParam(value = "group" , defaultValue = "") String group)
    {
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        List<MessageModel> listMess = null;
        try{
            listMess =  chatService.getConservation(group , 1);

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
