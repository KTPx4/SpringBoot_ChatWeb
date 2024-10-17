package com.Px4.ChatAPI.models.account;


import com.Px4.ChatAPI.controllers.requestParams.account.RegisterParams;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
public class AccountModel {
    @Id
    private String id;
    @Setter
    private String username;
    @Setter
    private String UserProfile;
    @Setter
    private String password;
    @Setter
    private String name;
    @Setter
    private String email;
    @Setter
    private String image ="/";
    @Setter
    private String status = "normal";
    @Setter
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

    public AccountModel(RegisterParams registerAcc) {
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
        this.UserProfile = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public String getUserProfile() {
        return UserProfile;
    }

}
