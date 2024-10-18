package com.Px4.ChatAPI.models.account;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "resetaccounts")
public class ResetModel {

    @Getter
    @Setter
    private String userId;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private Date createdAt;

    @Getter
    @Setter
    private String newPassword;

    public static int TIME_LVIE = 60*5;

    public ResetModel(String userId, String token, String newPassword) {
        this.userId = userId;
        this.token = token;
        createdAt = Date.from(Instant.now());
        this.newPassword = newPassword;
    }


}
