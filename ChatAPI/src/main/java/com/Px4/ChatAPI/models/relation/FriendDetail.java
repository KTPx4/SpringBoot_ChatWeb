package com.Px4.ChatAPI.models.relation;

import com.Px4.ChatAPI.models.message.MessageModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private String avatar;

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

    @Getter
    @Setter
    private List<MessageModel> listMessage;

    public FriendDetail() {
    }

    public FriendDetail(String id, String name, String userProfile, String avatar, String status, String createdAt, String type, boolean isFriend) {
        this.id = id;
        this.name = name;
        this.userProfile = userProfile;
        this.avatar = avatar;
        this.status = status;
        this.createdAt = createdAt;
        this.type = type;
        this.isFriend = isFriend;

    }


}
