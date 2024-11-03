package com.Px4.ChatAPI.services.chat;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.message.*;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.models.relation.GroupRepository;
import com.Px4.ChatAPI.services.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RelationService relationService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    private int  PAGE_SIZE = 20;

    public GroupModel canSendMess(String userID, String toGroupId) throws Exception
    {

       try{
           Optional<GroupModel> grModel = groupRepository.findById(toGroupId);
            GroupModel gr = null;
           if(grModel.isEmpty())
           {
               Optional<AccountModel> acc = accountRepository.findById(toGroupId);
               if(acc.isEmpty())  throw new Exception("conversation-Group/User not found");

               //GroupModel newGr = new GroupModel("chat", true, Arrays.asList(userID, toGroupId));
                gr = relationService.initGroup(userID, toGroupId);
           }else gr = grModel.get();

           if(gr.isPvP())
           {
               List<String> mem = gr.getMembers();
               String user1 = mem.get(0);
               String user2 = mem.get(1);


               if(!relationService.canChat(user1, user2)) throw new Exception("conversation-You has blocked or blocked by this user!");
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

        MessageModel messageModel = new MessageModel(conversation.getId(), userID, reply, contentType, content);
        messageModel = messageRepository.save(messageModel);
        return messageModel;
    }

    public List<MessageModel> getConservation(String groupId, int pageNumber) throws Exception
    {
        String userId = jwtRequestFilter.getIdfromJWT();
        Optional<GroupModel> grModel = groupRepository.findById(groupId);
        if(grModel.isEmpty()) throw new Exception("chat-Group not found");

        GroupModel gr = grModel.get();
        List<String> members = gr.getMembers();
        if(!members.contains(userId)) throw new Exception("chat-You not permission to access message this group");

        Optional<ConversationModel> ccModel = conversationRepository.findByGroupId(groupId);
        ConversationModel cv =null;
        if(ccModel.isEmpty())
        {
            cv = new ConversationModel(groupId);
            cv = conversationRepository.save(cv);
        }
        else cv = ccModel.get();
        // Kích thước trang là 10
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, PAGE_SIZE);

        Page<MessageModel> page = messageRepository.findByIdConversationOrderByCreatedAtDesc(cv.getId(), pageRequest);
        List<MessageModel> listMessage = page.getContent();
        //listMessage.forEach(m -> System.out.println(m.getContent()));

        List<MessageModel> modifiableList = new ArrayList<>(page.getContent());

        Px4Generate.sortMessagesByDate(modifiableList);
     //   modifiableList.forEach(m -> System.out.println(m.getContent() +" | " + Px4Generate.toHCMtime(m.getCreatedAt())));
        return modifiableList;
    }
}
