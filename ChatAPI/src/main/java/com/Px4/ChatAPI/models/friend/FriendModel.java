package com.Px4.ChatAPI.models.friend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

@Document(collection = "friends")
public class FriendModel{
    @Id
    private String id;

    @Setter
    @Getter
    private String accountID;

    @Setter
    @Getter
    private String friendID;

    @Setter
    @Getter
    private String status;

    @Setter
    @Getter
    private Date createdAt;

    @Setter
    @Getter
    private Boolean isFriend;

    @Setter
    @Getter
    private String Type; // non - waiting response - response waiting

    public static String typeNon = "non";
    public static String typeWaiting = "waiting";
    public static String typeResponse = "response";

    public FriendModel(String accountID, String friendID) {
        this.accountID = accountID;
        this.friendID = friendID;
        this.createdAt = Date.from(Instant.now());
        this.status = "normal";
        this.isFriend = false;
        this.Type = "non";
    }
}
