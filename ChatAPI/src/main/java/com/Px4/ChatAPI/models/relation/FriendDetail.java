package com.Px4.ChatAPI.models.relation;

import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.account.AccountModel;
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
    private String groupId;
    @Getter
    @Setter
    private List<MessageModel> listMessage;
    @Getter
    @Setter
    private int count;

    public FriendDetail() {
        this.count = 0;
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
        this.count =0;

    }
    public FriendDetail(AccountModel account, FriendModel friend)
    {
        this.id = account.getId();
        this.name = account.getName();
        this.userProfile = account.getUserProfile();
        this.avatar = account.getImage();
        this.status = friend.getStatus();
        this.createdAt = Px4Generate.toHCMtime(friend.getCreatedAt());
        this.type = friend.getType();
        this.isFriend = friend.getIsFriend();
        this.count = 0;
    }


}
