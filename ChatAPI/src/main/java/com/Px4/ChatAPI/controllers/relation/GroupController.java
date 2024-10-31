package com.Px4.ChatAPI.controllers.relation;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.controllers.requestParams.relation.RequestGroup;
import com.Px4.ChatAPI.controllers.requestParams.relation.ResponseGroup;
import com.Px4.ChatAPI.models.Px4Response;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.services.RelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(("/api/v1/group"))
public class GroupController {
    private static final Logger log = LoggerFactory.getLogger(GroupController.class);
    @Autowired
    RelationService relationService;

    @GetMapping()
    public ResponseEntity<Px4Response> getAll()
    {
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        List<GroupModel> listGr = null;

        try{

            listGr =  relationService.getAllGroup();
        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("group"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new Px4Response<>(mess, listGr), status);
    }
    @PostMapping()
    public ResponseEntity<Px4Response> createGroup(@RequestBody RequestGroup requestGroup)
    {
        String mess = ResponeMessage.createSuccess;
        HttpStatus status = HttpStatus.OK;
        ResponseGroup gr = null;

        try{
            if(requestGroup.getName() == null) throw new Exception("group-'name' is null");
            if(requestGroup.getUsers() == null || requestGroup.getUsers().size() < 2) throw new Exception("group-'users' list must at least 2 members id");
            gr =  relationService.createGroup(requestGroup);
        }
        catch(Exception e)
        {
            System.out.println(e);
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("group"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }
        // handle for send make friend - accept make fiend  + set isFriend = true
        return new ResponseEntity<>(new Px4Response<>(mess, gr), status);
    }


}
