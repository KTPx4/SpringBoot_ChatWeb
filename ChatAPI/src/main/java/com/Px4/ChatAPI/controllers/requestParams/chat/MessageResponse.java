package com.Px4.ChatAPI.controllers.requestParams.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MessageResponse {


    private String id;

    private String type;

    private String sender;
    private String senderName;
    private String avatar;

    private String to;

    private String content;

    private String contentType;

    private String replyMessageId;

    private Date createdAt;
    private  Boolean isSystem = false;

    private List<String> whoSeen = new ArrayList<>();


    public MessageResponse() {
    }

    public MessageResponse(String type, String sender, String to, String content, String contentType) {
        this.type = type;
        this.sender = sender;
        this.to = to;
        this.content = content;
        this.contentType = contentType;
        this.replyMessageId = "";
    }

    public MessageResponse(String type, String sender, String to, String content, String contentType, String replyMessageId) {
        this.type = type;
        this.sender = sender;
        this.to = to;
        this.content = content;
        this.contentType = contentType;
        this.replyMessageId = replyMessageId;
    }


}
