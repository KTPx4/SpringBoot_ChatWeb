package com.Px4.ChatAPI.models.message;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<MessageModel, String> {
}
