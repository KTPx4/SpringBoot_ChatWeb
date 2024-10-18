package com.Px4.ChatAPI.services;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.friend.FriendDetail;
import com.Px4.ChatAPI.models.friend.FriendModel;
import com.Px4.ChatAPI.models.friend.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private AccountRepository accountRepository;

    String typeNon = FriendModel.typeNon;
    String typeWait = FriendModel.typeWaiting;
    String typeResponse = FriendModel.typeResponse;

    public List<FriendDetail> getAllFriends()
    {
        String id = JwtRequestFilter.getIdfromJWT();
        List<FriendModel> friendList = friendRepository.findAllByAccountID(id);
        System.out.println( "Count: "+ friendList.stream().count());
        return null;
    }
    public FriendModel getById(String id) throws Exception
    {
        return null;
        // Optional<FriendModel> friend = friendRepository.fi(id);
    }

    private void checkUser(String user1, String user2) throws Exception
    {
        if(user1.equals(user2)) throw new Exception("friend-Can't action friend by selft");
        if(!accountRepository.existsById(user1)) throw new Exception("friend-Your account not found");
        if(!accountRepository.existsById(user2)) throw new Exception("friend-Your friend account not found");
    }



    private void setFriend(String user1, String user2, boolean isFriend) throws Exception
    {

        List<FriendModel> friendList = GetRelationShip(user1, user2);

        FriendModel user1Friend = friendList.get(0);
        user1Friend.setType(typeNon);
        user1Friend.setIsFriend(isFriend);


        FriendModel user2Friend = friendList.get(1);
        user2Friend.setType(typeNon);
        user2Friend.setIsFriend(isFriend);



        friendRepository.save(user1Friend);
        friendRepository.save(user2Friend);
    }

    private void setAction(String user1, String user2) throws Exception // user1 wait - user 2 respone
    {

        List<FriendModel> friendList = GetRelationShip(user1, user2);
        FriendModel user1Friend = friendList.get(0);
        user1Friend.setType(typeWait);

        FriendModel user2Friend = friendList.get(1);
        user2Friend.setType(typeResponse);

        friendRepository.save(user1Friend);
        friendRepository.save(user2Friend);
    }


    private List<FriendModel> GetRelationShip(String user1, String user2) throws Exception
    {
        checkUser(user1, user2);
        Optional<FriendModel> friendUser1 = friendRepository.findByAccountIDAndFriendID(user1, user2);
        Optional<FriendModel> friendUser2 = friendRepository.findByAccountIDAndFriendID(user2, user1);

        List<FriendModel> createList = new ArrayList<>();
        FriendModel newFriend1 = null;
        FriendModel newFriend2 = null;
        if(friendUser1.isEmpty()) // Create if not exists
        {
            newFriend1 = new FriendModel(user1, user2);
            friendRepository.save(newFriend1);
        }
        else newFriend1 = friendUser1.get();

        createList.add(newFriend1);

        if(friendUser2.isEmpty()) // Create for user 2 if not exists
        {
            newFriend2 = new FriendModel(user2, user1);
            friendRepository.save(newFriend2);
        }
        else newFriend2 = friendUser2.get();

        createList.add(newFriend2);

        return createList;

    }

    public boolean addFriend(String friendID) throws Exception
    {

        String idUser = JwtRequestFilter.getIdfromJWT();

        List<FriendModel> listRelation = GetRelationShip(idUser, friendID); // Get relationship with friendid / user 2

        FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1

        String type = Friend.getType().toLowerCase();

        if(type.equals(typeNon) && !Friend.getIsFriend())
        {
            setAction(idUser, friendID);
        }
        else if(type.equals(typeResponse)) //  response accept make friend
        {
            setFriend(idUser, friendID, true);
        }
        else // wait for make friend
        {
            throw new Exception("friend-Request has been sent or now is friend. Please wait!");
        }
        return true;
    }
}
