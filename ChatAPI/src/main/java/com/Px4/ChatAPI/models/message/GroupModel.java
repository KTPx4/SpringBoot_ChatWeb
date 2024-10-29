package com.Px4.ChatAPI.models.message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("groups")
public class GroupModel {
    @Id
    @Getter
    @Setter
    private String id;
    @Setter
    @Getter
    private boolean isPvP;

    @Getter
    private List<String> members;

    public GroupModel(String id, boolean isPvP) {
        this.id = id;
        this.isPvP = isPvP;
        this.members = new ArrayList<>();
    }
    public GroupModel(String id, boolean isPvP, List<String> members) {
        this.id = id;
        this.isPvP = isPvP;
        this.members = members;
    }
    public void addMember(String memberId)
    {
        members.add(memberId);
    }

    public void removeMember(String memberId)
    {
        members.remove(memberId);
    }

}
