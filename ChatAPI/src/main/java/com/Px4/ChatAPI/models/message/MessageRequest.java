package com.Px4.ChatAPI.models.message;


import lombok.Getter;
import lombok.Setter;

public class MessageRequest {
    @Getter
    @Setter
    private String sender;

    @Getter
    @Setter
    private String to; // id group

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private String contentType;

    @Getter
    @Setter
    private String replyMessageId;

    public MessageRequest() {
    }

    public MessageRequest(String sender, String to, String content, String contentType) {
        this.sender = sender;
        this.to = to;
        this.content = content;
        this.contentType = contentType;
        this.replyMessageId = "";
    }



    public MessageRequest(String sender, String to, String content, String contentType, String replyMessageId) {
        this.sender = sender;
        this.to = to;
        this.content = content;
        this.contentType = contentType;
        this.replyMessageId = replyMessageId;
    }




}
