package com.Px4.ChatAPI.services.chat;

import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.services.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FriendService friendService;

    public boolean canSendMess(String userID, String toUserID)
    {

       try{
           return friendService.canChat(userID, toUserID);
       }
       catch (Exception e)
       {
           System.out.println("Error at ChatService: " + e.getMessage() + " - UserID: " + userID + " - ToUserID: " + toUserID);
           return false;
       }

    }

}
