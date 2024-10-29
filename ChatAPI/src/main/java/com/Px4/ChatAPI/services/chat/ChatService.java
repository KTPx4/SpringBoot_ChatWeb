package com.Px4.ChatAPI.services.chat;

import com.Px4.ChatAPI.models.Px4Generate;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.message.*;
import com.Px4.ChatAPI.services.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FriendService friendService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConversationRepository conversationRepository;

    public List<String> canSendMess(String userID, String toGroupId) throws Exception
    {

       try{
           Optional<GroupModel> gr = groupRepository.findById(toGroupId);

           if(gr.isEmpty()) throw new Exception("conversation-Group/User not found");

           if(gr.get().isPvP())
           {
               List<String> mem = gr.get().getMembers();
               String user1 = mem.get(0);
               String user2 = mem.get(1);
               if(!friendService.canChat(user1, user2)) throw new Exception("conversation-You has blocked or blocked by this user!");
           }
           return gr.get().getMembers();

       }
       catch (Exception e)
       {
           System.out.println("Error at ChatService: " + e.getMessage() + " - UserID: " + userID + " - ToUserID: " + toGroupId);
           throw new Exception(e.getMessage());
       }
    }
    public MessageModel createMessage(String groupId, String userID, String contentType, String content, String reply)
    {
        Optional<ConversationModel> cv = conversationRepository.findById(groupId);
        ConversationModel conversation = null;

        // create conversation if not exists
        if(cv.isPresent()) conversation = cv.get();
        else{
            conversation = new ConversationModel(groupId);
            conversation = conversationRepository.save(conversation);
        }

        MessageModel messageModel = new MessageModel(conversation.getId(), userID, reply, contentType, content);
        messageModel = messageRepository.save(messageModel);
        return messageModel;
    }

}
