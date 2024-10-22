package com.Px4.ChatAPI.services;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.jwt.JwtUtil;
import com.Px4.ChatAPI.models.account.AccountModel;
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

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    String typeNon = FriendModel.typeNon;
    String typeWait = FriendModel.typeWaiting;
    String typeResponse = FriendModel.typeResponse;
    String statusBlocked = FriendModel.statusBlocked;
    String statusBlockedBy = FriendModel.statusBlockedBy;
    String statusNormal = FriendModel.statusNormal;


    public List<FriendDetail> getAllFriends() throws Exception
    {
        String id = jwtRequestFilter.getIdfromJWT();
        List<FriendDetail> list = new ArrayList<>();
        List<FriendModel> friendList = friendRepository.findAllByAccountID(id);

        friendList.forEach(friend->{
            FriendDetail friendDetail = getById(friend.getFriendID());

            if(friendDetail != null) list.add(friendDetail);

        });
        return list;
    }
    public FriendDetail getById(String idFriend)
    {
        String id = jwtRequestFilter.getIdfromJWT();
        FriendDetail friendDetail = null;
        Optional<FriendModel> friend = friendRepository.findByAccountIDAndFriendID(id, idFriend);

        if(friend.isPresent())
        {
            FriendModel friendModel = friend.get();
            Optional<AccountModel> acc = accountRepository.findById(friendModel.getFriendID());

            if(acc.isPresent())
            {
                AccountModel account = acc.get();
                friendDetail = new FriendDetail(
                        account.getId(), account.getName(),
                        account.getUserProfile(), account.getImage(),
                        friendModel.getStatus(), friendModel.getCreatedAt(),
                        friendModel.getType(),
                        friendModel.getIsFriend());
            }

        }

        return friendDetail;
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
    private void Blocked(String user1, String user2) throws Exception
    {
        List<FriendModel> friendList = GetRelationShip(user1, user2);
        FriendModel user1Friend = friendList.get(0);
        user1Friend.setStatus(statusBlocked);
        user1Friend.setType(typeNon);

        FriendModel user2Friend = friendList.get(1);
        user2Friend.setStatus(statusBlockedBy);
        user2Friend.setType(typeNon);

        friendRepository.save(user1Friend);
        friendRepository.save(user2Friend);
    }
    private void unBlocked(String user1, String user2) throws Exception
    {
        List<FriendModel> friendList = GetRelationShip(user1, user2);
        FriendModel user1Friend = friendList.get(0);
        user1Friend.setStatus(statusNormal);

        FriendModel user2Friend = friendList.get(1);
        user2Friend.setStatus(statusNormal);

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

        String idUser = jwtRequestFilter.getIdfromJWT();

        List<FriendModel> listRelation = GetRelationShip(idUser, friendID); // Get relationship with friendid / user 2

        FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1

        String type = Friend.getType().toLowerCase();
        String status = Friend.getStatus().toLowerCase();
System.out .println("Status:" + status);
        if(status.equals(statusBlockedBy.toLowerCase())) throw new Exception("friend-You has been blocked can't action");
        else if(status.equals(statusBlocked.toLowerCase())) throw new Exception("friend-You has been Blocked this user");

        if(type.equals(typeNon) && !Friend.getIsFriend())
        {
            setAction(idUser, friendID); // action is idUser send make friend, firendID response request
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

    public boolean unFriend(String friendID) throws Exception
    {
        String idUser = jwtRequestFilter.getIdfromJWT();
        List<FriendModel> listRelation = GetRelationShip(idUser, friendID); // Get relationship with friendid / user 2

        FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1
        if(!Friend.getIsFriend()) throw new Exception("friend-Now has been unfriend");
        setFriend(idUser, friendID, false);
        return true;
    }

    public boolean actionStatus(String friendID) throws Exception
    {
        String idUser = jwtRequestFilter.getIdfromJWT();
        List<FriendModel> listRelation = GetRelationShip(idUser, friendID); // Get relationship with friendid / user 2

        FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1
        if(Friend.getStatus().toLowerCase().equals(statusBlocked))
        {
            unBlocked(idUser, friendID);
        }
        else if(Friend.getStatus().toLowerCase().equals(statusNormal)){
            Blocked(idUser, friendID);
        }
        else throw new Exception("friend-You have been blocked by this user");
        return true;
    }
}
