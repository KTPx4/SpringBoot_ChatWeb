package com.Px4.ChatAPI.controllers.relation;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.controllers.requestParams.relation.FriendItem;
import com.Px4.ChatAPI.controllers.requestParams.relation.ResponseFriends;
import com.Px4.ChatAPI.controllers.requestParams.relation.ResponseSuggest;
import com.Px4.ChatAPI.models.Px4Response;
import com.Px4.ChatAPI.models.relation.FriendDetail;
import com.Px4.ChatAPI.services.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {
    @Autowired
    private RelationService relationService ;

    @GetMapping() // get list friend
    public ResponseEntity<Px4Response> getFriendList(){
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        ResponseFriends res = null;
        try{

           res = relationService.getAllFriends();

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
        return new ResponseEntity<>(new Px4Response<>(mess, res), status);
    }
    @GetMapping("/request/all")
    public ResponseEntity<Px4Response> getAllRequest(){
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;
        ResponseFriends res = null;
        try{

            res = relationService.getAllRequest();

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
        return new ResponseEntity<>(new Px4Response<>(mess, res), status);
    }
    @GetMapping("/suggest/all")
    public ResponseEntity<Px4Response> getAllSuggest(@RequestParam(value = "page" , defaultValue = "1") int page)
    {
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;
        ResponseSuggest res = null;
        try{

            res = relationService.getAllSuggest(page);

        }
        catch(Exception e)
        {
            e.printStackTrace();
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("friend"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new Px4Response<>(mess, res), status);
    }

    @PostMapping("/unfriend/{id}") // unfriend
    public ResponseEntity<Px4Response> unFriend(@PathVariable String id)
    {
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;
        FriendItem friendItem = null;
        try{

            friendItem = relationService.unFriend(id);

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
        return new ResponseEntity<>(new Px4Response<>(mess, friendItem), status);
    }

    @PostMapping("/status/{id}")  // block - unblock this user
    public ResponseEntity<Px4Response> actionStatus(@PathVariable String id)
    {

        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;
        FriendItem friendDetail = null;

        try{
            friendDetail = relationService.actionStatus(id);

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
        return new ResponseEntity<>(new Px4Response<>(mess, friendDetail), status);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Px4Response> getById(@PathVariable String id)
    {
        // get info this user
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        FriendItem friend = null;
        try{

            friend = relationService.getById(id);
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
        return new ResponseEntity<>(new Px4Response<>(mess, friend), status);
    }

    @PostMapping("/{id}") // make friend - send request and response
    public ResponseEntity<Px4Response> actionFriend(@PathVariable String id)
    {
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;
        FriendItem friendDetail = null;
        try{

            friendDetail = relationService.addFriend(id);

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
        return new ResponseEntity<>(new Px4Response<>(mess, friendDetail), status);
    }




}
