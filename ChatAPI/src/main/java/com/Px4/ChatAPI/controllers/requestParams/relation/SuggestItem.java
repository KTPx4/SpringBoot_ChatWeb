package com.Px4.ChatAPI.controllers.requestParams.relation;

import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.relation.FriendModel;

public class SuggestItem {
    private String id;
    private String name;
    private String avatar;
    private Boolean isFriend;
    private String type;
    private String status;

    public SuggestItem() {
    }

    public SuggestItem(String id, String name, String image, Boolean isFriend, String type, String status) {
        this.id = id;
        this.name = name;
        this.avatar = image;
        this.isFriend = isFriend;
        this.type = type;
        this.status = status;
    }
    public SuggestItem(AccountModel accountModel)
    {
        this.id = accountModel.getId();
        this.name = accountModel.getName();
        this.avatar = accountModel.getImage();
        this.isFriend = false;
        this.type = FriendModel.typeNon;
        this.status = FriendModel.statusNormal;
    }

    public SuggestItem(AccountModel accountModel, FriendModel friendModel)
    {
        this.id = accountModel.getId();
        this.name = accountModel.getName();
        this.avatar = accountModel.getImage();
        this.isFriend = friendModel.getIsFriend();
        this.type = friendModel.getType();
        this.status = friendModel.getStatus();
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getFriend() {
        return isFriend;
    }

    public void setFriend(Boolean friend) {
        isFriend = friend;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
