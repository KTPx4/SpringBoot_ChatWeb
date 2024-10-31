package com.Px4.ChatAPI.services;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.requestParams.relation.RequestGroup;
import com.Px4.ChatAPI.controllers.requestParams.relation.ResponseGroup;
import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.message.ConversationModel;
import com.Px4.ChatAPI.models.message.ConversationRepository;
import com.Px4.ChatAPI.models.relation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RelationService {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private GroupSettingRepository groupSettingRepository;

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

                GroupModel gr = initGroup(id, idFriend);

                friendDetail = new FriendDetail(
                        account.getId(), account.getName(),
                        account.getUserProfile(), account.getImage(),
                        friendModel.getStatus(), Px4Generate.toHCMtime(friendModel.getCreatedAt()),
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

    public  List<FriendModel> GetRelationShip(String user1, String user2) throws Exception
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

        initGroup(user1, user2);


        return createList;

    }
    public GroupModel initGroup(String user1, String user2)
    {

        List<GroupModel> groups = groupRepository.findAll();
        GroupModel newGroup = new GroupModel();

       for(GroupModel gr : groups)
       {

           if(gr.isPvP() && gr.getMembers().size() == 2 && gr.getMembers().contains(user1) && gr.getMembers().contains(user2))
           {

               newGroup.setId(gr.getId());
               newGroup.setName(gr.getName());
               newGroup.setPvP(gr.isPvP());
               newGroup.setMembers(gr.getMembers());
               break;
           }
       }

        //Create Group if not exists
        if(newGroup.getId() == null  || newGroup.getId().equals(""))
        {
            String name = "Chat";

            newGroup = new GroupModel(name, true, Arrays.asList(user1, user2));
            newGroup = groupRepository.save(newGroup);
        }

        GroupSettingModel groupSettingModel = groupSettingRepository.findByGroupId(newGroup.getId());

        if(groupSettingModel == null)
        {
            GroupSettingModel grSetting = new GroupSettingModel(newGroup.getId(), "non"); // setting for group
            grSetting = groupSettingRepository.save(grSetting);
        }

        // Create conversation of group if not exists
        Optional<ConversationModel> cv = conversationRepository.findByGroupId(newGroup.getId());
        if(cv.isEmpty())
        {

            ConversationModel conv = new ConversationModel(newGroup.getId());
            conv = conversationRepository.save(conv);
        }


        return newGroup;
    }
    public boolean addFriend(String friendID) throws Exception
    {

        String idUser = jwtRequestFilter.getIdfromJWT();

        List<FriendModel> listRelation = GetRelationShip(idUser, friendID); // Get relationship with friendid / user 2

        FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1

        String type = Friend.getType().toLowerCase();
        String status = Friend.getStatus().toLowerCase();

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

    public boolean isFriend(String friendID)
    {
        try{
            String idUser = jwtRequestFilter.getIdfromJWT();
            List<FriendModel> listRelation = GetRelationShip(idUser, friendID);
            FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1
            return Friend.getIsFriend();
        }
        catch (Exception e)
        {
            return false;
        }
    }
    public boolean isBlocked(String friendID)
    {
        try{
            String idUser = jwtRequestFilter.getIdfromJWT();
            List<FriendModel> listRelation = GetRelationShip(idUser, friendID);
            FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1
            return Friend.getStatus().toLowerCase().equals(statusBlocked) || Friend.getStatus().toLowerCase().equals(statusBlockedBy) ;
        }
        catch (Exception e)
        {
            return true;
        }
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

    public boolean canChat(String userId1, String userId2) throws Exception
    {

            List<FriendModel> friendList = GetRelationShip(userId1, userId2);
            FriendModel user1Friend = friendList.get(0);

            FriendModel user2Friend = friendList.get(1);

            return user1Friend.getStatus().toLowerCase().equals(statusNormal) &&  user2Friend.getStatus().toLowerCase().equals(statusNormal);
//        try{
//
//        }
//        catch (Exception e)
//        {
//            throw e;
//            return false;
//        }
    }

    public List<GroupModel> getAllGroup()
    {
        String userId = jwtRequestFilter.getIdfromJWT();
        System.out.println("user: " +userId);
        List<GroupModel> groupList = groupRepository.findAll();
        List<GroupModel> grResponse = new ArrayList<>();
        for(GroupModel group : groupList)
        {
            if(!group.isPvP() && group.getMembers().contains(userId))
            {
                grResponse.add(group);
            }
        }
        return grResponse;
    }

    public ResponseGroup createGroup(RequestGroup requestGroup) throws Exception
    {
        String userId = jwtRequestFilter.getIdfromJWT();
        List<String> members = requestGroup.getUsers();
        if(members.size() < 2) throw new Exception("group-Cant create group less than 2 members");

        List<String> listUser = new ArrayList<>();
        members.forEach(id->{
            if(accountRepository.existsById(id))
            {
                listUser.add(id);
            }
        });

        if(listUser.size() < 2) throw new Exception("group-Have user not found or can not create group");


        GroupModel gr = new GroupModel(requestGroup.getName(), listUser);

        gr = groupRepository.save(gr);

        GroupSettingModel grSetting = new GroupSettingModel(gr.getId(), userId); // setting for group
        grSetting = groupSettingRepository.save(grSetting);

        ConversationModel cv = new ConversationModel(gr.getId()); // create conversation for group
        cv = conversationRepository.save(cv);
        System.out.println("Relation Service - createGroup - create conversation");

        ResponseGroup response = new ResponseGroup(gr, grSetting);
        return response;
    }
}
