package com.Px4.ChatAPI.models.relation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document("group_settings")
public class GroupSettingModel {
    @Getter
    @Setter
    @Id
    private String id;

    @Getter
    @Setter
    private String groupId;

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

    public GroupSettingModel(String groupId, String leader){
        this.groupId = groupId;
        this.leaderId = leader;
        this.createdAt = Date.from(Instant.now());
        this.isAllPermit = true;
        this.canSend = new ArrayList<>();
    }
    public GroupSettingModel(){}

    public GroupSettingModel(String groupId, String leader, List<String> deputy){
        this.groupId = groupId;
        this.leaderId = leader;
        this.deputy = deputy;
        this.createdAt = Date.from(Instant.now());
        this.isAllPermit = true;
        this.canSend = new ArrayList<>();
    }


}
