package com.Px4.ChatAPI.models.account;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResetRepository extends MongoRepository<ResetModel, String> {
    ResetModel findByToken(String token);
    Boolean existsByToken(String token);
    Boolean existsByUserId(String userId);
}
