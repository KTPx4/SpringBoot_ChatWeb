package com.Px4.ChatAPI.services;

import com.Px4.ChatAPI.controllers.jwt.JwtRequestFilter;
import com.Px4.ChatAPI.controllers.requestParams.relation.GroupChatItem;
import com.Px4.ChatAPI.controllers.requestParams.relation.UpdateGroup;
import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.models.relation.GroupRepository;
import com.Px4.ChatAPI.models.relation.GroupSettingModel;
import com.Px4.ChatAPI.models.relation.GroupSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(grSetting == null)
        {
            grSetting = new GroupSettingModel(group.getId(), group.getMembers().getFirst());
            grSetting = groupSettingRepository.save(grSetting);
        }
        if(updateGroup.getName() != null && !updateGroup.getName().isEmpty())
        {
            group.setName(updateGroup.getName());
            group = groupRepository.save(group);
        }
        if(updateGroup.getAddMembers().size() > 0  && (grSetting.getLeaderId().equals(idUser) || grSetting.getDeputy().contains(idUser) || grSetting.isAllPermit() ))
        {
            group.addMember(updateGroup.getAddMembers());
        }

        if(updateGroup.getRemoveMembers().size() > 0 && (grSetting.getLeaderId().equals(idUser) || grSetting.getDeputy().contains(idUser) || grSetting.isAllPermit() ))
        {
            List<String> newMembers = group.getMembers()
                    .stream()
                    .filter(idMembers -> !updateGroup.getRemoveMembers().contains(idMembers))
                    .collect(Collectors.toList());

            group.setMembers(newMembers);
            group = groupRepository.save(group);
        }

        if(updateGroup.getDeputy().size() > 0)
        {
            grSetting.setDeputy(updateGroup.getDeputy());
            grSetting = groupSettingRepository.save(grSetting);
        }
        if((permis != null && !permis.isEmpty()) || (updateGroup.getPermis() != null && !updateGroup.getPermis().isEmpty()))
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
        return new GroupChatItem(group, grSetting);
    }
}
