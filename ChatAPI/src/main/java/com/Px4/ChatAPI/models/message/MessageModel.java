package com.Px4.ChatAPI.models.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Getter
    @Setter
    private boolean isSeen;

    @Setter
    @Getter
    List<String> whoSeen;

    public MessageModel(String idConversation, String sender, String contentType, String content)
    {
        this.idConversation = idConversation;
        this.sender = sender;
        this.contentType = contentType; // text, image, file
        this.content = content;
        this.reply = "";
        this.createdAt = Date.from(Instant.now());
        this.isSystem = false;
        this.isSeen = false;
        this.isDeleted = false;
        this.whoSeen = new ArrayList<>();
    }
    public MessageModel(String idConversation, String sender, String contentType, String content, boolean isSystem)
    {
        this.idConversation = idConversation;
        this.sender = sender;
        this.contentType = contentType; // text, image, file
        this.content = content;
        this.reply = "";
        this.createdAt = Date.from(Instant.now());
        this.isSystem = isSystem;
        this.isSeen = false;
        this.isDeleted = false;
        this.whoSeen = new ArrayList<>();
    }
    public MessageModel()
    {
        this.isSystem = false;
        this.isDeleted = false;
        this.isSeen = false;
        this.whoSeen = new ArrayList<>();

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
        this.isSeen = false;
        this.whoSeen = new ArrayList<>();
    }

}
