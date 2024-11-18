package com.Px4.ChatAPI.controllers.requestParams.relation;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ResponseGroupChat {
    @Getter
    @Setter
    private int count;
    @Getter
    @Setter
    private List<GroupChatItem> groups;

    public ResponseGroupChat() {
        this.count = 0;
        this.groups = new ArrayList<>();
    }
    public ResponseGroupChat(int count, List<GroupChatItem> groups)
    {
        this.count = count;
        this.groups = groups;
    }
}
