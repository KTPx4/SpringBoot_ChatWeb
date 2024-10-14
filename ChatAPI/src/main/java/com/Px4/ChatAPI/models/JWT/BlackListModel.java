package com.Px4.ChatAPI.models.JWT;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "BlackLists")
public class BlackListModel {
    @Id
    String id;
    String token;
}
