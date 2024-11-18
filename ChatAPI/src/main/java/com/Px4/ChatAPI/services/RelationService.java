package com.Px4.ChatAPI.services;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.requestParams.relation.*;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.message.ConversationModel;
import com.Px4.ChatAPI.models.message.ConversationRepository;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.message.MessageRepository;
import com.Px4.ChatAPI.models.relation.*;
import com.Px4.ChatAPI.services.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;




import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class RelationService {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    private ChatService chatService;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private GroupSettingRepository groupSettingRepository;
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private int  PAGE_SIZE = 20;

    public RelationService(@Lazy ChatService chatService) {
        this.chatService = chatService;
    }
    String typeNon = FriendModel.typeNon;
    String typeWait = FriendModel.typeWaiting;
    String typeResponse = FriendModel.typeResponse;

    String statusBlocked = FriendModel.statusBlocked;
    String statusBlockedBy = FriendModel.statusBlockedBy;
    String statusNormal = FriendModel.statusNormal;

    public GroupModel findGroupWithFriend(String friendId) throws Exception
    {
        Query query = new Query();
        String userID = jwtRequestFilter.getIdfromJWT();
        query.addCriteria(Criteria.where("members").all(Arrays.asList( friendId,userID)));
        GroupModel grs =  mongoTemplate.findOne(query, GroupModel.class);
        return grs;
    }
    public ResponseFriends getAllRequest() throws  Exception
    {
        String userID = jwtRequestFilter.getIdfromJWT();
        List<FriendItem> list= new ArrayList<>();
        Query query = new Query();

        query.addCriteria(Criteria.where("accountID").is(userID))
                .addCriteria(Criteria.where("isFriend").is(false))
                .addCriteria(Criteria.where("Type").is(FriendModel.typeResponse))
                .addCriteria(Criteria.where("status").in(FriendModel.statusNormal, FriendModel.statusBlocked, FriendModel.statusBlockedBy));

        List<FriendModel> friendList =  mongoTemplate.find(query, FriendModel.class);
        friendList.forEach(friend->{
            String friendId = friend.getFriendID();
            AccountModel accFriend = accountRepository.findById(friendId).orElse(null);
            if(accFriend != null)
            {
                FriendItem friendItem = new FriendItem(accFriend, friend);

                list.add(friendItem);
            }

        });
        return new ResponseFriends(list.size(), list);
    }
    public long countNotFriend(String userID) {
        Query query = new Query();
        query
                .addCriteria(Criteria.where("accountID").is(userID))
                .addCriteria(Criteria.where("isFriend").is(false))
                .addCriteria(Criteria.where("status").ne(FriendModel.statusBlockedBy));

        return mongoTemplate.count(query, FriendModel.class);
    }

    public ResponseSuggest getAllSuggest(int page) throws  Exception
    {
        String userID = jwtRequestFilter.getIdfromJWT();
        int skip = (page - 1) * PAGE_SIZE;
        Query query = new Query();
        query
            .addCriteria(Criteria.where("accountID").is(userID))
            .addCriteria(Criteria.where("isFriend").is(false))
            .addCriteria(Criteria.where("status").ne(FriendModel.statusBlockedBy))
            .limit(PAGE_SIZE)
            .skip(skip); // page from 1, but in db start from 0


        List<FriendModel> friendList =  mongoTemplate.find(query, FriendModel.class);
        List<SuggestItem> listSuggest = new ArrayList<>();
        friendList.forEach(friend->{
            String friendId = friend.getFriendID();
            AccountModel accFriend = accountRepository.findById(friendId).orElse(null);
            if(accFriend != null)
            {
                SuggestItem friendItem = new SuggestItem(accFriend, friend);
//                System.out.println("Friend: "+ accFriend.getName());
                listSuggest.add(friendItem);
            }
        });

        if(listSuggest.size() < PAGE_SIZE) // if  get
        {
            long countFriendModel = countNotFriend(userID);

            long exceptBound = 0;
            long need = 0;

            if(countFriendModel >= PAGE_SIZE)
            {
                exceptBound = countFriendModel / PAGE_SIZE;
                need = PAGE_SIZE - (countFriendModel % PAGE_SIZE); ;
            }
            else {
                need = PAGE_SIZE - listSuggest.size();
            }

            if(page - exceptBound >= 0)
            {
                page = page - (int) exceptBound -1 ; // -1 because page of user start from 1
                long skipAcc = 0;
                long limitAcc = need;

                if(page == 0)
                {
                    skipAcc = 0;
                    limitAcc = need;
                }
                else if(page == 1)
                {
                    skipAcc = PAGE_SIZE*(page-1) + need;
                    limitAcc = PAGE_SIZE + need +  (page-1) * PAGE_SIZE;
                }
                else{
                    skipAcc = PAGE_SIZE + need + (long) (page - 2) * PAGE_SIZE;
                    limitAcc = PAGE_SIZE + need + (long) (page - 1) * PAGE_SIZE;
                }

                System.out.println( "listSuggest.size(): " +listSuggest.size()+" skip " + skipAcc + " - limit:" + limitAcc);

                Aggregation aggregation = newAggregation(
                        // Lookup để ghép bảng `accounts` với bảng `friends`
                        lookup("friends", "_id", "accountID", "friendInfoByAccount"),
//                        lookup("friends", "_id", "friendID", "friendInfoFriend"),


                        // Lọc bỏ các tài khoản đã có liên kết trong bảng `friends` (với userID)
                        match(new Criteria().andOperator(
                                Criteria.where("_id").ne(userID), // Loại trừ tài khoản chính của người dùng

                                new Criteria().orOperator(
                                        // Điều kiện thỏa mãn chỉ cần userID không có trong `accountID` hoặc `friendID`
                                        Criteria.where("friendInfoByAccount").is(null), // Nếu không có thông tin friendInfoByAccount
                                        new Criteria().andOperator(
                                                Criteria.where("friendInfoByAccount.friendID").ne(userID), // Hoặc nếu friendID không chứa userID
                                                Criteria.where("friendInfoByAccount.accountID").ne(userID) // Hoặc nếu accountID không chứa userID
                                        )
                                )
                        )),

//                         Nhóm kết quả theo `_id` để loại bỏ các bản ghi trùng lặp
                        group("_id")
                                .first("_id").as("id")
                                .first("name").as("name")
                                .first("image").as("image"),
//                        project("_id", "name", "image"),

                        // Giới hạn số bản ghi trả về theo kích thước trang
                        limit(limitAcc),
                        skip(skipAcc)
                );

                List<AccountModel> listAcc = mongoTemplate.aggregate(aggregation, "accounts", AccountModel.class).getMappedResults();

                listAcc.forEach(acc ->{
                    SuggestItem item = new SuggestItem(acc);
//                    System.out.println("Acc: "+ acc.getName());
                    listSuggest.add(item);
                });
            }


        }


        return new ResponseSuggest(listSuggest.size(), listSuggest);
    }

    public ResponseFriends getAllFriends() throws Exception
    {
        String id = jwtRequestFilter.getIdfromJWT();
        List<FriendItem> list = new ArrayList<>();

        Query query = new Query();
        query.addCriteria(Criteria.where("accountID").is(id))
                .addCriteria(Criteria.where("isFriend").is(true));
        List<FriendModel> friendList = mongoTemplate.find(query, FriendModel.class);
//        List<FriendModel> friendList = friendRepository.findAllByAccountID(id);

        friendList.forEach(friend->{
            String friendId = friend.getFriendID();
            AccountModel accFriend = accountRepository.findById(friendId).orElse(null);
            if(accFriend != null)
            {
                FriendDetail friendDetail = new FriendDetail(accFriend, friend);
                try{
                    GroupModel gr = findGroupWithFriend(friendId);
                    if(gr == null || gr.getId() == null) gr = initGroup(id,friendId);
                   // List<MessageModel> listMess = chatService.getConservation(gr.getId(), 1);
//                    int count = 0;
//                    for(MessageModel mess : listMess)
//                    {
//                        if(!mess.getSender().equals(id))
//                        {
//                            count += mess.isSeen()? 0 : 1;
//                        }
//                        mess.setCreatedAt(Px4Generate.toDateHCM(mess.getCreatedAt()));
//                    }
//                    friendDetail.setListMessage(listMess);
//                    friendDetail.setCount(count);

                    FriendItem friendItem = new FriendItem(friendDetail);

                    list.add(friendItem);
                }
                catch (Exception e)
                {

                }
            }

        });
        return new ResponseFriends(friendList.stream().count() ,list);
    }
    public List<FriendItem> searchByName(String userId, String name)
    {
        List<FriendItem> friendDetail = new ArrayList<>();
        try{

            // Tạo biểu thức chính quy cho tên cần tìm, với tùy chọn không phân biệt hoa thường
            Query query = new Query();
            query.addCriteria(
                    new Criteria().andOperator(
                            Criteria.where("name").regex(".*" + name + ".*", "i"),
                            Criteria.where("_id").ne(userId)
                    )
            );

            if( name == null || name.isEmpty())
            {

            }
            // Thực hiện truy vấn và trả về danh sách kết quả
            List<AccountModel> list =  mongoTemplate.find(query, AccountModel.class);
            list.forEach(acc ->{
                try{
                    List<FriendModel> relation = GetRelationShip(userId, acc.getId());
                    FriendModel friendModel = relation.getFirst();
                    if(!friendModel.getStatus().toLowerCase().equals(FriendModel.statusBlockedBy))
                    {

                        FriendItem friendItem = new FriendItem(acc, friendModel);
                        friendDetail.add(friendItem);
                    }
                }
                catch (Exception e)
                {

                }
            });

        }
        catch (Exception e)
        {
            return null;
        }

        return friendDetail;
    }

    public FriendItem getById(String userId, String friendId)
    {
        FriendItem friendDetail = null;
        try{

            List<FriendModel> friends =  GetRelationShip(userId, friendId);

            FriendModel friendModel = friends.getFirst();

            if(friendModel != null && !friendModel.getStatus().toLowerCase().equals(FriendModel.statusBlockedBy))
            {
                AccountModel account = accountRepository.findById(friendId).get();
                friendDetail = new FriendItem(account, friendModel);
            }

        }
        catch (Exception e)
        {
            return null;
        }

        return friendDetail;
    }

    public FriendItem getById(String idFriend)
    {
        String id = jwtRequestFilter.getIdfromJWT();
        return getById(id,idFriend);
    }

    private void checkUser(String user1, String user2) throws Exception
    {
        if(user1.equals(user2)) throw new Exception("friend-Can't action friend by selft");
        if(!accountRepository.existsById(user1)) throw new Exception("friend-Your account not found");
        if(!accountRepository.existsById(user2)) throw new Exception("friend-Your friend account not found");
    }

    private FriendModel setFriend(String user1, String user2, boolean isFriend) throws Exception
    {

        List<FriendModel> friendList = GetRelationShip(user1, user2);

        FriendModel user1Friend = friendList.get(0);
        user1Friend.setType(typeNon);
        user1Friend.setIsFriend(isFriend);


        FriendModel user2Friend = friendList.get(1);
        user2Friend.setType(typeNon);
        user2Friend.setIsFriend(isFriend);


        user1Friend = friendRepository.save(user1Friend);
        friendRepository.save(user2Friend);
        return user1Friend;
    }

    private FriendModel setAction(String user1, String user2) throws Exception // user1 wait - user 2 respone
    {

        List<FriendModel> friendList = GetRelationShip(user1, user2);
        FriendModel user1Friend = friendList.get(0);
        user1Friend.setType(typeWait);

        FriendModel user2Friend = friendList.get(1);
        user2Friend.setType(typeResponse);

        user1Friend = friendRepository.save(user1Friend);
        user2Friend = friendRepository.save(user2Friend);
        return user1Friend;
    }
    private FriendModel Blocked(String user1, String user2) throws Exception
    {
        List<FriendModel> friendList = GetRelationShip(user1, user2);
        FriendModel user1Friend = friendList.get(0);
        user1Friend.setStatus(statusBlocked);
        user1Friend.setType(typeNon);

        FriendModel user2Friend = friendList.get(1);
        user2Friend.setStatus(statusBlockedBy);
        user2Friend.setType(typeNon);

        user1Friend = friendRepository.save(user1Friend);
        friendRepository.save(user2Friend);

        return user1Friend;
    }
    private FriendModel unBlocked(String user1, String user2) throws Exception
    {
        List<FriendModel> friendList = GetRelationShip(user1, user2);
        FriendModel user1Friend = friendList.get(0);
        user1Friend.setStatus(statusNormal);

        FriendModel user2Friend = friendList.get(1);
        user2Friend.setStatus(statusNormal);

        user1Friend = friendRepository.save(user1Friend);
        friendRepository.save(user2Friend);
        return user1Friend;
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
            newFriend1 = friendRepository.save(newFriend1);
        }
        else newFriend1 = friendUser1.get();

        createList.add(newFriend1);

        if(friendUser2.isEmpty()) // Create for user 2 if not exists
        {
            newFriend2 = new FriendModel(user2, user1);
            newFriend2 = friendRepository.save(newFriend2);
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
    public FriendItem addFriend(String idUser, String friendID) throws  Exception
    {
        List<FriendModel> listRelation = GetRelationShip(idUser, friendID); // Get relationship with friendid / user 2

        FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1

        AccountModel accFriend = accountRepository.findById(friendID).get();


        String type = Friend.getType().toLowerCase();
        String status = Friend.getStatus().toLowerCase();

        if(status.equals(statusBlockedBy.toLowerCase())) throw new Exception("friend-You has been blocked can't action");
        else if(status.equals(statusBlocked.toLowerCase())) throw new Exception("friend-You has been Blocked this user");

        if(type.equals(typeNon) && !Friend.getIsFriend())
        {
            Friend = setAction(idUser, friendID); // action is idUser send make friend, firendID response request
        }
        else if(type.equals(typeResponse)) //  response accept make friend
        {
            Friend = setFriend(idUser, friendID, true);
        }
        else if(type.equals(typeWait) || Friend.getIsFriend()) // cancel make friend
        {
            Friend = setFriend(idUser, friendID, false);
        }
        else // wait for make friend
        {
            throw new Exception("friend-Request has been sent or now is friend. Please wait!");
        }

        FriendItem friendDetail = new FriendItem(accFriend, Friend);

        return friendDetail;
    }

    public FriendItem addFriend(String friendID) throws Exception
    {
        String idUser = jwtRequestFilter.getIdfromJWT();
        return addFriend(idUser, friendID);

    }

    public FriendItem unFriend(String friendID) throws Exception
    {
        String idUser = jwtRequestFilter.getIdfromJWT();
        List<FriendModel> listRelation = GetRelationShip(idUser, friendID); // Get relationship with friendid / user 2

        FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1

        Friend =  setFriend(idUser, friendID, false);
        AccountModel accountModel = accountRepository.findById(friendID).get();
        FriendItem friendDetail = new FriendItem(accountModel, Friend);
        return friendDetail;
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

    public FriendItem actionStatus(String friendID) throws Exception
    {
        String idUser = jwtRequestFilter.getIdfromJWT();
        List<FriendModel> listRelation = GetRelationShip(idUser, friendID); // Get relationship with friendid / user 2

        FriendModel Friend = listRelation.getFirst(); // friend model of idUser: 0 - friend model of friendID: 1
        if(Friend.getStatus().toLowerCase().equals(statusBlocked))
        {
            Friend = unBlocked(idUser, friendID);
        }
        else if(Friend.getStatus().toLowerCase().equals(statusNormal)){
            Friend = Blocked(idUser, friendID);
        }
        else throw new Exception("friend-You have been blocked by this user");

        AccountModel accFriend = accountRepository.findById(friendID).get();
        FriendItem friendDetail = new FriendItem(accFriend, Friend);
        return friendDetail;
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

        Query query = new Query();
        query.addCriteria(Criteria.where( "isPvP").is(false)
                                    .and("members").in(userId));

        List<GroupModel> grResponse = mongoTemplate.find(query, GroupModel.class);

        return grResponse;
    }

    public GroupChatItem createGroup(RequestGroup requestGroup) throws Exception
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
        MessageModel messCreate = new MessageModel(cv.getId(), "server", "text", "Create group success. Let started chat", true);

        messCreate =  messageRepository.save(messCreate);

        System.out.println("Relation Service - createGroup - create conversation");

        GroupChatItem grI = new GroupChatItem(gr);
        grI.addMessage(messCreate);

        return grI;
    }
}
