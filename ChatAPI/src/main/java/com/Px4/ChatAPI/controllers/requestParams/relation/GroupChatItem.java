package com.Px4.ChatAPI.controllers.requestParams.relation;

import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.relation.GroupModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class GroupChatItem {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String avatar;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private boolean isPvP= false;
    @Getter
    @Setter
    private int count = 0;
    @Getter
    @Setter
    private boolean selected = false;
    @Getter
    @Setter
    private List<String> members = new ArrayList<>();

    @Getter
    @Setter
    private List<MessageModel> messages = new ArrayList<>();

    public GroupChatItem() {
    }

    public GroupChatItem(GroupModel gr)
    {
        this.id = gr.getId();
        this.avatar = gr.getAvatar();
        this.name = gr.getName();
        this.members = gr.getMembers();
    }

    public void addMessage(MessageModel message)
    {
        this.messages.add(message);
    }

}
