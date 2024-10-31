package com.Px4.ChatAPI.models.relation;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupSettingRepository extends MongoRepository<GroupSettingModel, String> {
    GroupSettingModel findByGroupId(String groupId);
}
