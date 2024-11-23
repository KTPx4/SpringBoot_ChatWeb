package com.Px4.ChatAPI.controllers.requestParams.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfo {
    private String id;
    private String name;
    private String avatar;

    public AccountInfo() {
    }

    public AccountInfo(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }
}
