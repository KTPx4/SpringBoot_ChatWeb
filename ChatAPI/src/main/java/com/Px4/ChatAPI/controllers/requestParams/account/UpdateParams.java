package com.Px4.ChatAPI.controllers.requestParams.account;

public class UpdateParams {
    private String name;
    private String avatar;
    private String email;
    private String userProfile;

    public UpdateParams() {
    }

    public String getName() {
        return name;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
