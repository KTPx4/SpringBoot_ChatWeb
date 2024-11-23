package com.Px4.ChatAPI.services.chat;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.requestParams.account.AccountInfo;
import com.Px4.ChatAPI.controllers.requestParams.relation.GroupChatItem;
import com.Px4.ChatAPI.controllers.requestParams.relation.FriendItem;
import com.Px4.ChatAPI.controllers.requestParams.relation.ResponseFriends;
import com.Px4.ChatAPI.controllers.requestParams.relation.ResponseGroupChat;
import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.message.*;
import com.Px4.ChatAPI.models.relation.*;
import com.Px4.ChatAPI.services.RelationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private AccountRepository accountRepository;


    private RelationService relationService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private MongoTemplate mongoTemplate;
    private int  PAGE_SIZE = 15;
    @Autowired
    private FriendRepository friendRepository;
    @Autowired
    private GroupSettingRepository groupSettingRepository;

    public ChatService(@Lazy RelationService relationService) {
        this.relationService = relationService;
    }

    public GroupModel canSendMess(String userID, String toGroupId) throws Exception
    {

       try{

           Optional<GroupModel> grModel = groupRepository.findById(toGroupId);
            GroupModel gr = null;
           if(grModel.isEmpty())
           {
               Optional<AccountModel> acc = accountRepository.findById(toGroupId);
               if(acc.isEmpty()) throw new Exception("conversation-Group/User not found");
               //GroupModel newGr = new GroupModel("chat", true, Arrays.asList(userID, toGroupId));
                gr = relationService.initGroup(userID, toGroupId);

           }else gr = grModel.get();

           if(!gr.getMembers().contains(userID))  throw new Exception("conversation-User not allowed to send message");

           if(gr.isPvP()) // chat 2 pvp
           {
               List<String> mem = gr.getMembers();
               String user1 = mem.get(0);
               String user2 = mem.get(1);


               if(!relationService.canChat(user1, user2)) throw new Exception("conversation-You has blocked or blocked by this user!");
           }
           else{ // group
                GroupSettingModel grSetting = groupSettingRepository.findByGroupId(gr.getId());
                List<String> listCanSend = grSetting.getCanSend();
                if(listCanSend.size() > 0 && !listCanSend.contains(userID)) throw new Exception("conversation-You can't send message in this group");
           }
           return gr;

       }
       catch (Exception e)
       {
           e.printStackTrace();
           System.out.println("Error at ChatService: " + e.getMessage() + " - UserID: " + userID + " - ToUserID: " + toGroupId);
           throw new Exception(e.getMessage());
       }
    }

    public MessageModel createMessage(String groupId, String userID, String contentType, String content, String reply)
    {
        Optional<ConversationModel> cv = conversationRepository.findByGroupId(groupId);
        ConversationModel conversation = null;

        // create conversation if not exists
        if(cv.isPresent()) conversation = cv.get();
        else{
            System.out.println("Chat Service - createMessage - create conversation");
            conversation = new ConversationModel(groupId);
            conversation = conversationRepository.save(conversation);
        }
        AccountModel acc = accountRepository.findById(userID).get();
        MessageModel messageModel = new MessageModel(conversation.getId(), userID, reply, contentType, content);
        messageModel.setAvatar(acc.getImage());
        messageModel.setSenderName(acc.getName());
        messageModel = messageRepository.save(messageModel);
        return messageModel;
    }

    public boolean setSeen(String groupId, String userID) throws Exception
    {
        //System.out.println(groupId);
        ConversationModel Cv = conversationRepository.findByGroupId(groupId).get();
        // Cập nhật tất cả các tin nhắn trong nhóm mà sender không phải userID
        Query query = new Query();
        query.addCriteria(Criteria.where("idConversation").is(Cv.getId()));
        query.addCriteria(Criteria.where("sender").ne(userID)); // Chỉ cập nhật tin nhắn không phải của userID


        Update update = new Update();
        update.set("isSeen", true);  // Cập nhật trường seen thành true
        update.addToSet("whoSeen", userID); // Thêm userID vào mảng whoSeen nếu chưa có

        // Thực hiện cập nhật trong MongoDB mà không cần tải hết các tin nhắn về
        mongoTemplate.updateMulti(query, update, MessageModel.class);

        return true;
    }

    public List<MessageModel> getConservation(String groupId, int pageNumber) throws Exception
    {
        String userId = jwtRequestFilter.getIdfromJWT();
        Optional<GroupModel> grModel = groupRepository.findById(groupId);
        if(grModel.isEmpty()) throw new Exception("chat-Group not found");

        GroupModel gr = grModel.get();
        List<String> members = gr.getMembers();
        if(!members.contains(userId)) throw new Exception("chat-You not permission to access message this group");

        ConversationModel cv = conversationRepository.findByGroupId(groupId)
                .orElseGet(() -> {
                    ConversationModel newCv = new ConversationModel(groupId);
                    return conversationRepository.save(newCv);
                }
        );




        List<MessageModel> modifiableList = new ArrayList<>();

        if(cv != null)
        {
            // Kích thước trang là 10
            PageRequest pageRequest = PageRequest.of(pageNumber - 1, PAGE_SIZE);

            Page<MessageModel> page = messageRepository.findByIdConversationOrderByCreatedAtDesc(cv.getId(), pageRequest);

            List<MessageModel> listMessage = page.getContent();

            if(listMessage != null && !listMessage.isEmpty())
            {
                modifiableList = new ArrayList<>(page.getContent());

                Px4Generate.sortMessagesByDate(modifiableList);
            }
        }

     //   modifiableList.forEach(m -> System.out.println(m.getContent() +" | " + Px4Generate.toHCMtime(m.getCreatedAt())));
        return modifiableList;
    }

    public Optional<MessageModel> getMessageById(String id)
    {
        return messageRepository.findById(id);
    }
    public ResponseFriends getAllChat(boolean isPvP)
    {

        String userId = jwtRequestFilter.getIdfromJWT();

        Query query = new Query();
        query.addCriteria(Criteria.where("members").in(userId)).addCriteria(Criteria.where("isPvP").is(isPvP));
        List<GroupModel> listGr = mongoTemplate.find(query, GroupModel.class);

        if(listGr.isEmpty())
        {
            return new ResponseFriends(0, null);
        }

        List<FriendItem> rs = listGr.stream().map(
                gr ->{
                    List<String> members = gr.getMembers();


                    Query queryConv = new Query();
                    queryConv.addCriteria(Criteria.where("groupId").is(gr.getId()));
                    ConversationModel Conv = mongoTemplate.findOne(queryConv, ConversationModel.class);
                    if(Conv == null ) return  null;

                    // Kích thước trang là 10
                    PageRequest pageRequest = PageRequest.of(1 - 1, PAGE_SIZE);

                    Page<MessageModel> page = messageRepository.findByIdConversationOrderByCreatedAtDesc(Conv.getId(), pageRequest);

                    List<MessageModel> listMessage = page.getContent();

                    if(listMessage.isEmpty()) return null;

                    List<MessageModel> modifiableList = new ArrayList<>(page.getContent());

                    Px4Generate.sortMessagesByDate(modifiableList);

                    // get detail of friend
                    String friendId = members.get(0).equals(userId) ? members.get(1) : members.get(0);

                    AccountModel accFriend = accountRepository.findById(friendId).orElse(null);
                    if(accFriend == null) return null;

                    FriendModel friendModel = friendRepository.findByAccountIDAndFriendID(userId, friendId).orElse(null);

                    FriendDetail friendDetail = new FriendDetail(accFriend, friendModel);

                    friendDetail.setGroupId(gr.getId());

                    friendDetail.setListMessage(modifiableList);

                    //count not seen message
                    int countSent = 0;
                    if(isPvP)
                    {
                        countSent = (int) modifiableList.stream()
                                .filter(m -> !m.getSender().equals(userId) && !m.isSeen())  // Lọc các phần tử có sender khác userId
                                .count();
                    }
                    else{
                        countSent = (int) modifiableList.stream()
                                .filter(m -> !m.getSender().equals(userId) && !m.getWhoSeen().contains(userId))  // Lọc các phần tử có sender khác userId
                                .count();
                    }

                    friendDetail.setCount(countSent);

                    // convert to item of response
                    FriendItem result = new FriendItem(friendDetail);
                    result.setMembers(gr.getMembers());
                    return result;
                }
        ).filter(result -> result != null).collect(Collectors.toList());

        // Sắp xếp danh sách FriendItem trước khi trả về
        rs.sort((item1, item2) -> {
            // So sánh theo count trước (giảm dần)
            int countComparison = Long.compare(item2.getCount(), item1.getCount());
            if (countComparison != 0) {
                return countComparison;
            }

            // Nếu count bằng nhau, so sánh theo createdAt của tin nhắn mới nhất (gần nhất)
            // Lấy tin nhắn mới nhất trong danh sách messages của mỗi FriendItem
            MessageModel latestMessage1 = item1.getMessages().isEmpty() ? null : item1.getMessages().get(item1.getMessages().size() - 1);
            MessageModel latestMessage2 = item2.getMessages().isEmpty() ? null : item2.getMessages().get(item2.getMessages().size() - 1);

            if (latestMessage1 == null && latestMessage2 == null) {
                return 0; // Không có tin nhắn trong cả hai, giữ nguyên vị trí
            } else if (latestMessage1 == null) {
                return 1; // Đưa item1 xuống dưới
            } else if (latestMessage2 == null) {
                return -1; // Đưa item2 xuống dưới
            }

            // So sánh thời gian của tin nhắn mới nhất
            return latestMessage2.getCreatedAt().compareTo(latestMessage1.getCreatedAt());
        });
        
        return new ResponseFriends(rs.size(), rs);
/*
        // find conv
        Query queryConv = new Query();
        queryConv.addCriteria(Criteria.where("groupId").in(grIds));
        List<ConversationModel> listConv = mongoTemplate.find(queryConv, ConversationModel.class);

        // get chat from friend, not take group !!!!
        List<Result> friendDetails = listConv.stream().map(
                conversation -> {
                    Query messageQuery;
                    messageQuery = new Query();
                    messageQuery.addCriteria(Criteria.where("idConversation").is(conversation.getId()));
                    List<MessageModel> messages = mongoTemplate.find(messageQuery, MessageModel.class);

                    if (!messages.isEmpty()) {
                        AccountModel accountModel = mongoTemplate.findById(conversation.getGroupId(), AccountModel.class); // giả sử bạn có cách để lấy thông tin tài khoản

                        FriendDetail friendDetail = new FriendDetail();
                        Result rs = new Result();
                     //   friendDetail.setId(accountModel.getId());
                        friendDetail.setName(accountModel.getName());
                        friendDetail.setUserProfile(accountModel.getUserProfile());
                        friendDetail.setAvatar(accountModel.getImage());
                        friendDetail.setStatus("Active"); // giả sử bạn có cách để xác định trạng thái
                        friendDetail.setCreatedAt(Px4Generate.toHCMtime(conversation.getCreatedAt()));
                        friendDetail.setType("Group");
                        friendDetail.setFriend(true); // giả sử bạn có cách để xác định người bạn
                        friendDetail.setGroupId(conversation.getGroupId());
                        friendDetail.setListMessage(messages);
                        friendDetail.setCount(messages.size());
                        return rs;
                    }

                    return null;
                }).filter(friendDetail -> friendDetail != null).collect(Collectors.toList());
*/
    }

    public ResponseGroupChat getAllChatGroup()
    {
        String userId = jwtRequestFilter.getIdfromJWT();

        Query query = new Query();
        query.addCriteria(Criteria.where("members").in(userId)).addCriteria(Criteria.where("isPvP").is(false));
        List<GroupModel> listGr = mongoTemplate.find(query, GroupModel.class);
        if( listGr == null || listGr.isEmpty() )
        {
            return new ResponseGroupChat();
        }

        List<GroupChatItem> groupChatItems = new ArrayList<>();

        listGr.forEach(gr->{
            GroupChatItem groupChatItem = new GroupChatItem(gr);

            gr.getMembers().forEach(memberId ->{
                AccountModel acc = accountRepository.findById(memberId).orElse(null);
                if(acc != null) groupChatItem.addMemberV2(acc);
            });
            
            Query queryConv = new Query();
            queryConv.addCriteria(Criteria.where("groupId").is(gr.getId()));
            ConversationModel Conv = mongoTemplate.findOne(queryConv, ConversationModel.class);
            if(Conv != null)
            {
                // Kích thước trang là 10
                PageRequest pageRequest = PageRequest.of(1 - 1, PAGE_SIZE);

                Page<MessageModel> page = messageRepository.findByIdConversationOrderByCreatedAtDesc(Conv.getId(), pageRequest);

                List<MessageModel> listMessage = page.getContent();

                if(listMessage != null && !listMessage.isEmpty())
                {
                    List<MessageModel> modifiableList = new ArrayList<>(page.getContent());

                    Px4Generate.sortMessagesByDate(modifiableList);
                    int countSent = (int) modifiableList.stream()
                            .filter(m -> !m.getSender().equals(userId) && !m.getWhoSeen().contains(userId) && !m.isSystem())  // Lọc các phần tử có sender khác userId
                            .count();

                    groupChatItem.setMessages(modifiableList);
                    groupChatItem.setCount(countSent);

                }
            }

            groupChatItems.add(groupChatItem);

            Query querySetting = new Query();
            querySetting.addCriteria(Criteria.where("groupId").is(gr.getId()));
            GroupSettingModel groupSetting = mongoTemplate.findOne(querySetting, GroupSettingModel.class);
            if(groupSetting != null)
            {
                groupChatItem.setSettings(groupSetting);
            }

        });
        return new ResponseGroupChat(groupChatItems.size(), groupChatItems);
    }
    public List<MessageModel> getFriendChat(String friendId, int pageNumber)
    {
        String userId = jwtRequestFilter.getIdfromJWT();
        Query query = new Query();
        query.addCriteria(Criteria.where("members").all(Arrays.asList( friendId,friendId)))
            .addCriteria(Criteria.where("isPvP").is(true));

        GroupModel gr = mongoTemplate.findOne(query, GroupModel.class);
        if(gr == null)
        {
            return null;
        }

        Query queryConv = new Query();
        queryConv.addCriteria(Criteria.where("groupId").is(gr.getId()));
        ConversationModel Conv = mongoTemplate.findOne(queryConv, ConversationModel.class);
        if(Conv == null ) return  null;

        // Kích thước trang là 10
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, PAGE_SIZE);

        Page<MessageModel> page = messageRepository.findByIdConversationOrderByCreatedAtDesc(Conv.getId(), pageRequest);

        List<MessageModel> listMessage = page.getContent();

        if(listMessage.isEmpty()) return null;

        List<MessageModel> modifiableList = new ArrayList<>(page.getContent());

        Px4Generate.sortMessagesByDate(modifiableList);

        return modifiableList;
    }


    @Getter
    @Setter
    public class Result{
        private FriendDetail friendDetail;
        private List<MessageModel> messages;
    }


}
