package com.Px4.ChatAPI.controllers.requestParams.relation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class RequestGroup {
    @Getter
    @Setter
    private String name;

    List<String> users;

    public void setUsers(List<String> users) {this.users = users;}
    public List<String> getUsers()
    {
        return users;
    }
    public void addUser(String user)
    {
        users.add(user);
    }

}
