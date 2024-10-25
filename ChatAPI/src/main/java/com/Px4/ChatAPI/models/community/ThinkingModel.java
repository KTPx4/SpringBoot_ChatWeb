package com.Px4.ChatAPI.models.community;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "think_posts")
public class ThinkingModel
{
    @Id
    @Getter
    private String Id;

    @Getter
    @Setter
    private String accountID;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Date  createdAt;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private List<String> shows;

    public ThinkingModel(String accountID, String title) {
        this.accountID = accountID;
        this.title = title;
        this.createdAt = Date.from(Instant.now());
        shows = new ArrayList<>();
        type = "public";
    }

}
