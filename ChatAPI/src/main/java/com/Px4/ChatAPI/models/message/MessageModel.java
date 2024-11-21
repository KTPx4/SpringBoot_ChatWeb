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
@Getter
@Setter
public class MessageModel {
    @Id

    private String id;

    private String idConversation;

    private String sender;
    private String senderName;

    private String avatar;

    private String reply;

    private String content;

    private String contentType;

    private Date createdAt;

    private boolean isSystem;

    private boolean isDeleted;

    private boolean isSeen;

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
