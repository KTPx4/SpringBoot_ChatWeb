package com.Px4.ChatAPI.controllers.requestParams.relation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
public class UpdateGroup {
    private String id;
    private List<String> addMembers = new ArrayList<String>();
    private List<String> removeMembers = new ArrayList<String>();
    private List<String> canSend = new ArrayList<String>();
    private List<String> deputy = new ArrayList<String>();
    private String leader;
    private String name;
    private String permis;

    public UpdateGroup() {}


}
