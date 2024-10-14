package com.Px4.ChatAPI.models.account;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
public class AccountModel {
    @Id
    private String id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String image ="/";
    private String status = "normal";
    private String role = "USER";


    // Constructor không tham số
    public AccountModel() {
    }
    public AccountModel(String user, String pass, String name, String email) {
        this.username = user;
        this.password = pass;
        this.email = email;
        this.name = name;
    }

    public AccountModel(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = username;
    }

    public AccountModel(RegisterModel registerAcc) {
        this.username = registerAcc.getUsername();
        this.password = registerAcc.getPassword();
        this.email = registerAcc.getEmail();
        this.name = this.username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
