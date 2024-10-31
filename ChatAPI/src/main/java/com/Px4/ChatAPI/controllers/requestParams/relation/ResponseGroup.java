package com.Px4.ChatAPI.controllers.requestParams.relation;

import com.Px4.ChatAPI.models.relation.GroupModel;
import com.Px4.ChatAPI.models.relation.GroupSettingModel;
import lombok.Getter;
import lombok.Setter;

public class ResponseGroup {
    @Getter
    @Setter
    private GroupModel groupModel;
    @Getter
    @Setter
    private GroupSettingModel groupSettingModel;
    public ResponseGroup(GroupModel groupModel, GroupSettingModel groupSettingModel) {
         this.groupModel = groupModel;
         this.groupSettingModel = groupSettingModel;
    }
}
