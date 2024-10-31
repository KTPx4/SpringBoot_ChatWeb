package com.Px4.ChatAPI.services.chat;

import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.message.*;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.models.relation.GroupRepository;
import com.Px4.ChatAPI.services.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
