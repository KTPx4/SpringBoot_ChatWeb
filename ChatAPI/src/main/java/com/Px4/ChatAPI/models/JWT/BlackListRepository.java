package com.Px4.ChatAPI.models.JWT;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BlackListRepository extends MongoRepository<BlackListModel, String> {

    Optional<BlackListModel> findByToken(String token);
    boolean existsByToken(String token);
}
