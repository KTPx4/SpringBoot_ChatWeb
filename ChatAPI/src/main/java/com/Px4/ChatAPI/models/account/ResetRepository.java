package com.Px4.ChatAPI.models.account;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ResetRepository extends MongoRepository<ResetModel, String> {
    Optional<ResetModel> findByToken(String token);
    Boolean existsByToken(String token);
    Boolean existsByUserId(String userId);
}
