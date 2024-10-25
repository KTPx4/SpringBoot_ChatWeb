package com.Px4.ChatAPI.models.friend;

import java.util.Date;

public class FriendDetail {
    private String id;
    private String name;
    private String userProfile;
    private String image;

    private String status;
    private String createdAt;
    private String type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
