package com.Px4.ChatAPI.controllers.friend;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.models.BaseRespone;
import com.Px4.ChatAPI.models.friend.FriendDetail;
import com.Px4.ChatAPI.models.friend.FriendModel;
import com.Px4.ChatAPI.services.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @GetMapping() // get list friend
    public ResponseEntity<BaseRespone> getFriendList(){
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;
        List<FriendDetail> listFriend = null;
        try{

           listFriend = friendService.getAllFriends();

        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("friend"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new BaseRespone<>(mess, listFriend), status);
    }

    @PostMapping("/unfriend/{id}") // unfriend
    public ResponseEntity<BaseRespone> unFriend(@PathVariable String id)
    {
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;

        try{

            friendService.unFriend(id);

        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("friend"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new BaseRespone<>(mess, null), status);
    }

    @PostMapping("/status/{id}")  // block - unblock this user
    public ResponseEntity<BaseRespone> actionStatus(@PathVariable String id)
    {

        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;

        try{
            friendService.actionStatus(id);

        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("friend"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new BaseRespone<>(mess, null), status);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BaseRespone> getById(@PathVariable String id)
    {
        // get info this user
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;
        FriendDetail friend = null;
        try{

            friend = friendService.getById(id);

        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("friend"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new BaseRespone<>(mess, friend), status);
    }

    @PostMapping("/{id}") // make friend - send request and response
    public ResponseEntity<BaseRespone> actionFriend(@PathVariable String id)
    {
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;

        try{

            friendService.addFriend(id);

        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("friend"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new BaseRespone<>(mess, null), status);
    }




}
