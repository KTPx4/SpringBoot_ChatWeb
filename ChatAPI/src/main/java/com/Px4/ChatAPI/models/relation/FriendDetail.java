package com.Px4.ChatAPI.models.relation;

import lombok.Getter;
import lombok.Setter;

public class FriendDetail {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String userProfile;
    @Getter
    @Setter
    private String image;

    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private String createdAt;
    @Getter
    @Setter
    private String type;
    @Getter
    @Setter
    private boolean isFriend;

    public FriendDetail() {
    }

    public FriendDetail(String id, String name, String userProfile, String image, String status, String createdAt, String type, boolean isFriend) {
        this.id = id;
        this.name = name;
        this.userProfile = userProfile;
        this.image = image;
        this.status = status;
        this.createdAt = createdAt;
        this.type = type;
        this.isFriend = isFriend;

    }


}
