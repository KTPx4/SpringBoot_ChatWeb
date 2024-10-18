package com.Px4.ChatAPI.controllers.friend;

import com.Px4.ChatAPI.models.BaseRespone;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {

    @GetMapping()
    public ResponseEntity<BaseRespone> getFriendList(){

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
        // handle for send make friend - accept make fiend  + set isFriend = true
        return null;
    }



}
