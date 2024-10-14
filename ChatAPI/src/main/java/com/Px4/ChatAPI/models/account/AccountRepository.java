package com.Px4.ChatAPI.models.account;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<AccountModel, String> {
    Optional<AccountModel> findByUsername(String username);


}