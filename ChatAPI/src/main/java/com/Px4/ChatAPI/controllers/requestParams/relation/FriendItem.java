package com.Px4.ChatAPI.controllers.requestParams.relation;

import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.relation.FriendDetail;
import com.Px4.ChatAPI.models.relation.FriendModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class FriendItem {
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
    private List<MessageModel> messages;
    @Getter
    @Setter
    private int count;
    @Getter
    @Setter
    private boolean selected;

    public FriendItem() {
        this.count = 0;
        this.selected = false;

    }
    public FriendItem(FriendDetail friendDetail)
    {
        this.id = friendDetail.getId();
        this.name = friendDetail.getName();
        this.userProfile = friendDetail.getUserProfile();
        this.avatar = friendDetail.getAvatar();
        this.status = friendDetail.getStatus();
        this.createdAt = friendDetail.getCreatedAt();
        this.type = friendDetail.getType();
        this.isFriend = friendDetail.isFriend();
        this.groupId = friendDetail.getGroupId();
        this.messages = friendDetail.getListMessage();
        this.count = friendDetail.getCount();
        this.selected = false;
    }
    public FriendItem(AccountModel accountFriend, FriendModel friendModel)
    {

        this.id = accountFriend.getId();
        this.name = accountFriend.getName();
        this.userProfile = accountFriend.getUserProfile();
        this.avatar = accountFriend.getImage();
        this.status = friendModel.getStatus();
        this.createdAt = Px4Generate.toHCMtime(friendModel.getCreatedAt());
        this.type = friendModel.getType();
        this.isFriend = friendModel.getIsFriend();
        this.count = 0;
        this.selected = false;

    }
    public FriendItem(String id, String name, String userProfile, String avatar, String status, String createdAt, String type, boolean isFriend) {
        this.id = id;
        this.name = name;
        this.userProfile = userProfile;
        this.avatar = avatar;
        this.status = status;
        this.createdAt = createdAt;
        this.type = type;
        this.isFriend = isFriend;
        this.count =0;
        this.selected = false;

    }

    public String toJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
