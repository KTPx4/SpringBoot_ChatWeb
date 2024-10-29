package com.Px4.ChatAPI.models.message;

public class MessageResponse {
    private String id;
    private String type;
    private String sender;
    private String to;
    private String content;
    private String contentType;
    private String replyMessageId;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getReplyMessageId() {
        return replyMessageId;
    }

    public void setReplyMessageId(String replyMessageId) {
        this.replyMessageId = replyMessageId;
    }
}
