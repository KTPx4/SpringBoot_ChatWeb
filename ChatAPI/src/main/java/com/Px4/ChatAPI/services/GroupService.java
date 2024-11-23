package com.Px4.ChatAPI.services;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.requestParams.account.AccountInfo;
import com.Px4.ChatAPI.controllers.requestParams.relation.GroupChatItem;
import com.Px4.ChatAPI.controllers.requestParams.relation.UpdateGroup;
import com.Px4.ChatAPI.models.account.AccountModel;
import com.Px4.ChatAPI.models.account.AccountRepository;
import com.Px4.ChatAPI.models.message.ConversationModel;
import com.Px4.ChatAPI.models.message.ConversationRepository;
import com.Px4.ChatAPI.models.message.MessageModel;
import com.Px4.ChatAPI.models.message.MessageRepository;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.models.relation.GroupRepository;
import com.Px4.ChatAPI.models.relation.GroupSettingModel;
import com.Px4.ChatAPI.models.relation.GroupSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupSettingRepository groupSettingRepository;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageRepository messageRepository;

    public GroupModel updateAvt(String groupId, String path) throws Exception
    {
        GroupModel group = groupRepository.findById(groupId).get();
        if(group == null) throw new Exception("Group not found");

        group.setAvatar(path);

        return groupRepository.save(group);

    }

    public GroupChatItem update(String id, UpdateGroup updateGroup, String permis) throws Exception
    {
        GroupModel group = groupRepository.findById(id).get();
        String idUser = jwtRequestFilter.getIdfromJWT();
        if(group == null) throw new Exception("group-Group not found");
        GroupSettingModel grSetting = groupSettingRepository.findByGroupId(group.getId());

        GroupChatItem grItem = new GroupChatItem();
        ConversationModel cv = conversationRepository.findByGroupId(group.getId()).get();

        if(grSetting == null)
        {
            grSetting = new GroupSettingModel(group.getId(), group.getMembers().getFirst());
            grSetting = groupSettingRepository.save(grSetting);
        }

        // update leader
        if(updateGroup.getLeader() != null && grSetting.getLeaderId().equals(idUser))
        {
            grSetting.setLeaderId(updateGroup.getLeader());
            grSetting = groupSettingRepository.save(grSetting);
        }

        // update name
        if(updateGroup.getName() != null && !updateGroup.getName().isEmpty())
        {
            group.setName(updateGroup.getName());
            group = groupRepository.save(group);
        }

        // add member
        if(updateGroup.getAddMembers().size() > 0
                && (
                (grSetting.getLeaderId().equals(idUser)
                        || (grSetting.getDeputy() != null && grSetting.getDeputy().contains(idUser))
                        || grSetting.isAllPermit() )
                )
        )
        {
            List<String> list = updateGroup.getAddMembers();
            group.addMember(list);
            group = groupRepository.save(group);

            List<AccountInfo> accs = new ArrayList<>();
            list.forEach(ida ->{
                AccountModel accModel = accountRepository.findById(ida).get();
                if(accModel != null)
                {
                    accs.add(new AccountInfo(accModel.getId(), accModel.getName(), accModel.getImage()));
                    MessageModel messCreate = new MessageModel(cv.getId(), "server", "text", accModel.getName() + " Has been added", true);
                    messCreate =  messageRepository.save(messCreate);

                }
            });

            grItem.setListAdd(accs);
        }


        //remove member
        if  (updateGroup.getRemoveMembers().size() > 0
                && (
                        grSetting.getLeaderId().equals(idUser)
                        || (grSetting.getDeputy() != null && grSetting.getDeputy().contains(idUser))
//                        || grSetting.isAllPermit()
                        || (updateGroup.getRemoveMembers().size() == 1 && updateGroup.getRemoveMembers().contains(idUser))
                    )
            )
        {
            List<String> list = updateGroup.getRemoveMembers();

            List<String> newMembers = group.getMembers()
                    .stream()
                    .filter(idMembers -> !list.contains(idMembers))
                    .collect(Collectors.toList());

            if(newMembers.size() > 0 && !newMembers.contains(grSetting.getLeaderId()))
            {
                grSetting.setLeaderId(newMembers.getFirst());
            }

            group.setMembers(newMembers);
            group = groupRepository.save(group);
            grSetting = groupSettingRepository.save(grSetting);

            List<AccountInfo> accs = new ArrayList<>();
            list.forEach(ida ->{
                AccountModel accModel = accountRepository.findById(ida).get();
                if(accModel != null)
                {
                    accs.add(new AccountInfo(accModel.getId(), accModel.getName(), accModel.getImage()));
                    MessageModel messCreate = new MessageModel(cv.getId(), "server", "text", accModel.getName() + " Has been deleted", true);
                    messCreate =  messageRepository.save(messCreate);
                }
            });

            grItem.setListRemove(accs);
        }


        // add deputy
        if(updateGroup.getAddDeputy().size() > 0
                && (
                (grSetting.getLeaderId().equals(idUser)
                        || (grSetting.getDeputy() != null && grSetting.getDeputy().contains(idUser))
                        || grSetting.isAllPermit() )
                )
        )
        {
            List<String> list = updateGroup.getAddDeputy();
            grSetting.addDeputy(list);
            grSetting = groupSettingRepository.save(grSetting);


        }

        //remove deputy
        if(updateGroup.getRemoveDeputy().size() > 0
                && (
                               grSetting.getLeaderId().equals(idUser)
                                || (grSetting.getDeputy() != null && grSetting.getDeputy().contains(idUser))
                                || grSetting.isAllPermit()

                )
        )
        {
            List<String> newDeputy = grSetting.getDeputy()
                    .stream()
                    .filter((idA) -> !updateGroup.getRemoveDeputy().contains(idA))
                    .collect(Collectors.toList());
            grSetting.setDeputy(newDeputy);
            grSetting = groupSettingRepository.save(grSetting);
        }


        // set permit
        if((permis != null && !permis.isEmpty()) || (updateGroup.getPermis() != null && !updateGroup.getPermis().isEmpty())
                && (
                    (grSetting.getLeaderId().equals(idUser)
                            || (grSetting.getDeputy() != null && grSetting.getDeputy().contains(idUser))
                            || grSetting.isAllPermit() )
                )
        )
        {
            if((updateGroup.getPermis() != null && !updateGroup.getPermis().isEmpty())) permis = updateGroup.getPermis();

            if(permis.equals("true"))
            {
                grSetting.setAllPermit(true);
                grSetting = groupSettingRepository.save(grSetting);
            }
            else if(permis.equals("false"))
            {
                grSetting.setAllPermit(false);
                grSetting = groupSettingRepository.save(grSetting);
            }
        }

        grItem.setValue(group, grSetting);

        group.getMembers().forEach(memberId ->{
            AccountModel acc = accountRepository.findById(memberId).orElse(null);
            if(acc != null) grItem.addMemberV2(acc);
        });

        return grItem;
    }
    public boolean canGetMess(String group, String user)
    {
        GroupModel gr = groupRepository.findById(group).orElse(null);
        return gr != null && gr.getMembers().contains(user);
    }
}
