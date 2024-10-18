package com.Px4.ChatAPI.models.friend;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends MongoRepository<FriendModel, String> {
    public List<FriendModel> findAllByAccountID(String accountID);

    @Query("{'accountID': ?0, 'friendID': ?1}")
    Optional<FriendModel> findByAccountIDAndFriendID(String accountID, String friendID);
}
