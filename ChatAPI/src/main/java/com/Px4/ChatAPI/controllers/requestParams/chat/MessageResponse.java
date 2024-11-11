package com.Px4.ChatAPI.controllers.requestParams.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class MessageResponse {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String type;
    @Getter
    @Setter
    private String sender;
    @Getter
    @Setter
    private String to;
    @Getter
    @Setter
    private String content;
    @Getter
    @Setter
    private String contentType;
    @Getter
    @Setter
    private String replyMessageId;
    @Getter
    @Setter
    private Date createdAt;



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
