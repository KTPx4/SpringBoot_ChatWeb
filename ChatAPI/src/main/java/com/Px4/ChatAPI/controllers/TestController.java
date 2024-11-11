package com.Px4.ChatAPI.controllers;

import com.Px4.ChatAPI.models.Px4Response;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.services.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @Autowired
    private RelationService relationService;
    @GetMapping
    public ResponseEntity<Px4Response> test() {
        GroupModel gr = null;
        try{
            gr = relationService.findGroupWithFriend("9NWc4Griqbs");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Px4Response res = new Px4Response<>("TEst", gr);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
