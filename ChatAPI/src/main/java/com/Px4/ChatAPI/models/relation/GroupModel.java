package com.Px4.ChatAPI.models.relation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document("groups")
public class GroupModel {
    @Id
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String name;
    @Setter
    @Getter
    private boolean isPvP;

    @Getter
    @Setter
    private String avatar= "https://api.dicebear.com/9.x/icons/svg?seed=Maria";

    @Getter
    @Setter
    private List<String> members;

    @Getter
    @Setter
    private Date createdAt = Date.from(Instant.now());

    public GroupModel(String name,  List<String> members) {
        this.name = name;
        this.isPvP = false;
        this.members = members;

    }
    public GroupModel(String name, boolean isPvP, List<String> members) {
        this.name = name;
        this.isPvP = isPvP;
        this.members = members;
    }
    public GroupModel(GroupModel group)
    {
        this.id = group.getId();
        this.name = group.getName();
        this.isPvP = group.isPvP();
        this.members = group.getMembers();

    }

    public GroupModel() {}

    public void addMember(String memberId)
    {
        members.add(memberId);
    }

    public void addMember(List<String> members)
    {
        this.members.addAll(members);
    }

    public void removeMember(String memberId)
    {
        members.remove(memberId);
    }


}
