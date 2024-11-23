package com.Px4.ChatAPI.controllers.requestParams.relation;

import com.Px4.ChatAPI.controllers.requestParams.account.AccountInfo;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.models.relation.GroupSettingModel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GroupChatItem {

    private String id;

    private String avatar;

    private String name;

    private boolean isPvP= false;

    private int count = 0;

    private boolean selected = false;

    private List<String> members = new ArrayList<>();

    private List<AccountInfo> membersV2 = new ArrayList<>();


    private List<MessageModel> messages = new ArrayList<>();


    private String leaderId;

    private List<String>  deputy;


    private Date createdAt;

    private boolean isAllPermit;

    private List<String> canSend;

    private List<AccountInfo> listAdd = new ArrayList<>();
    private List<AccountInfo> listRemove = new ArrayList<>();
    private List<AccountInfo> listAddDeputy = new ArrayList<>();
    private List<AccountInfo> listRemoveDeputy = new ArrayList<>();

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
    public void setValue(GroupModel gr, GroupSettingModel settings)
    {
        this.id = gr.getId();
        this.avatar = gr.getAvatar();
        this.name = gr.getName();
        this.members = gr.getMembers();
        setSettings(settings);
    }
    public void addMemberV2(AccountInfo member)
    {
        this.membersV2.add(member);
    }
    public void addMemberV2(AccountModel accountModel)
    {
        this.membersV2.add(new AccountInfo(accountModel.getId(), accountModel.getName(), accountModel.getImage()));
    }
}
