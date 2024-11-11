package com.Px4.ChatAPI.controllers.requestParams.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SeenRequest {
    @Getter
    @Setter
    private String to; // id group

    public SeenRequest() {
    }

    public SeenRequest(String to) {
        this.to = to;
    }
}
