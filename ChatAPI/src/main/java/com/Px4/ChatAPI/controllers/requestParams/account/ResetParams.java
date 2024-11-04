package com.Px4.ChatAPI.controllers.requestParams.account;

import lombok.Getter;
import lombok.Setter;

public class ResetParams {
    @Getter
    @Setter
    private String username;
    public ResetParams(String username) {
        this.username = username;
    }
    public ResetParams() {}

}
