package com.Px4.ChatAPI.models.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

@Document(collection = "BlackLists")
public class BlackListModel {
    @Getter
    @Setter
    @Id
    long id;

    @Getter
    @Setter
    String token;

    @Getter
    @Setter
    private Date createdAt;

    public static long TIME_LIVE = 60 * 60 * 24 * 7;

    // Constructor, Getters, and Setters
    public BlackListModel(long id, String token, Date createdAt) {
        this.id = id;
        this.token = token;
        this.createdAt = createdAt;
    }

    public static BlackListModel createWithCurrentTime(long id, String token) {
        // Lấy thời gian hiện tại tại múi giờ địa phương (Asia/Ho_Chi_Minh)
        Date localDateTime = Date.from(Instant.now());

        return new BlackListModel(id, token, localDateTime);
    }


}
