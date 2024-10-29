package com.Px4.ChatAPI.models.message;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConversationRepository extends MongoRepository<ConversationModel, String> {
    Optional<ConversationModel> findByGroupId(String groupId);
}
