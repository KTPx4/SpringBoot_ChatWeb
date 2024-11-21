package com.Px4.ChatAPI.controllers.requestParams.relation;

import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.models.relation.GroupSettingModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
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

    @Getter
    @Setter
    private String leaderId;

    @Getter
    @Setter
    private List<String>  deputy;

    @Getter
    @Setter
    private Date createdAt;

    @Getter
    @Setter
    private boolean isAllPermit;

    @Getter
    @Setter
    private List<String> canSend;

    public GroupChatItem() {
    }

    public GroupChatItem(GroupModel gr)
    {
        this.id = gr.getId();
        this.avatar = gr.getAvatar();
        this.name = gr.getName();
        this.members = gr.getMembers();
    }
    public GroupChatItem(GroupModel gr, GroupSettingModel settings)
    {
        this.id = gr.getId();
        this.avatar = gr.getAvatar();
        this.name = gr.getName();
        this.members = gr.getMembers();
        setSettings(settings);
    }

    public void addMessage(MessageModel message)
    {
        this.messages.add(message);
    }

    public void setSettings(GroupSettingModel settings)
    {
        this.leaderId = settings.getLeaderId();
        this.deputy = settings.getDeputy();
        this.canSend = settings.getCanSend();
        this.isAllPermit = settings.isAllPermit();
        this.createdAt = settings.getCreatedAt();
    }

}
