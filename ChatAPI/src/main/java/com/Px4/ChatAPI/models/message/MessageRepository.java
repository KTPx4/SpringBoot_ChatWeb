package com.Px4.ChatAPI.models.message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<MessageModel, String> {
    List<MessageModel> findByIdConversation(String conversationId);
    Page<MessageModel> findByIdConversationOrderByCreatedAtDesc(String conversationId, Pageable pageable);
}
