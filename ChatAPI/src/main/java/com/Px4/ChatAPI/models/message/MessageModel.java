package com.Px4.ChatAPI.models.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

@Document("messages")
public class MessageModel {
    @Id
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String idConversation;
    @Getter
    @Setter
    private String sender;
    @Getter
    @Setter
    private String reply;
    @Getter
    @Setter
    private String content;
    @Getter
    @Setter
    private String contentType;
    @Getter
    @Setter
    private Date createdAt;
    @Getter
    @Setter
    private boolean isSystem;
    @Getter
    @Setter
    private boolean isDeleted;

    public MessageModel(String idConversation, String sender, String contentType, String content)
    {
        this.idConversation = idConversation;
        this.sender = sender;
        this.contentType = contentType;
        this.content = content;
        this.reply = "";
        this.createdAt = Date.from(Instant.now());
        this.isSystem = false;
        this.isDeleted = false;
    }

    public MessageModel(String idConversation, String sender,String reply, String contentType, String content)
    {
        this.idConversation = idConversation;
        this.sender = sender;
        this.contentType = contentType;
        this.content = content;
        this.reply = reply;
        this.createdAt = Date.from(Instant.now());
        this.isSystem = false;
        this.isDeleted = false;
    }

}
