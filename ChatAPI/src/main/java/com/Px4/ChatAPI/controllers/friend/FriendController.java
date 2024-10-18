package com.Px4.ChatAPI.controllers.friend;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.models.BaseRespone;
import com.Px4.ChatAPI.services.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {
    @Autowired
    private FriendService friendService;

    @GetMapping()
    public ResponseEntity<BaseRespone> getFriendList(){
        friendService.getAllFriends();
        return null;
    }

    @PostMapping("/unfriend/{id}")
    public ResponseEntity<BaseRespone> unFriend(@PathVariable String id)
    {
        // unfriend this user
        return null;
    }

    @PostMapping("/status/{id}")
    public ResponseEntity<BaseRespone> actionStatus(@PathVariable String id)
    {
        // block - unblock this user
        return null;
    }


    @GetMapping("/{id}")
    public ResponseEntity<BaseRespone> getById(@PathVariable String id)
    {
        // get info this user
        return null;
    }

    @PostMapping("/{id}")
    public ResponseEntity<BaseRespone> actionFriend(@PathVariable String id)
    {
        String mess = ResponeMessage.makeFriendRequest;
        HttpStatus status = HttpStatus.OK;

        try{
            System.out.println("Post");

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
