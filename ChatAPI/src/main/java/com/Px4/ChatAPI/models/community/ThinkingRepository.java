package com.Px4.ChatAPI.models.community;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ThinkingRepository extends MongoRepository<ThinkingModel, String> {
    Optional<ThinkingModel> findByAccountID(String accountID);
}
