package com.Px4.ChatAPI.controllers.requestParams.relation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ResponseFriends {
    @Getter
    @Setter
    private long count;
    @Getter
    @Setter
    private List<FriendItem> friends;

    public ResponseFriends() {}
    public ResponseFriends(long count, List<FriendItem> friends) {
        this.count = count;
        this.friends = friends;
    }
}
