package com.Px4.ChatAPI.models.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Document("conversations")
public class ConversationModel {
    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String groupId;

    @Getter
    @Setter
    private boolean isStored;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private Date createdAt;

    public ConversationModel(String groupId) {
        this.groupId = groupId;
        isStored = false;
        createdAt = Date.from(Instant.now());
        status = "normal";
    }
}
