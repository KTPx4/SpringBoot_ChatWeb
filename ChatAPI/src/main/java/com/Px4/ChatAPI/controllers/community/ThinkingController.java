package com.Px4.ChatAPI.controllers.community;

import com.Px4.ChatAPI.config.ResponeMessage;
import com.Px4.ChatAPI.models.Px4Response;
import com.Px4.ChatAPI.models.community.ThinkingModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/community/thinking")
public class ThinkingController {

    // 1 account - 1 thinking post in 1 time

    @GetMapping()
    public ResponseEntity<Px4Response> getMyThinking()
    {
        String mess = ResponeMessage.getSucce;
        HttpStatus status = HttpStatus.OK;
        ThinkingModel thinking = null;
        try{



        }
        catch(Exception e)
        {
            mess = ResponeMessage.SystemError;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            if(e.getMessage().startsWith("community"))
            {
                mess = e.getMessage().split("-")[1];
                status = HttpStatus.BAD_REQUEST;
            }
        }

        return new ResponseEntity<>(new Px4Response<>(mess, thinking), status);
    }

}
