package com.Px4.ChatAPI.controllers.requestParams.relation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ResponseSuggest {
    @Getter
    @Setter
    private long count;
    @Getter
    @Setter
    private List<SuggestItem> friends;

    public ResponseSuggest() {
    }

    public ResponseSuggest(long count, List<SuggestItem> friends) {
        this.count = count;
        this.friends = friends;
    }
}
