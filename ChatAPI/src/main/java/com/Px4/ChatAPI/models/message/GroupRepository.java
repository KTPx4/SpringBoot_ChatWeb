package com.Px4.ChatAPI.models.message;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<GroupModel, String> {
    // Custom query to find groups containing all member IDs in the given list
    // Query to find a group containing exactly two specific members, order-independent
    // Query to find a group containing exactly two specific members, order-independent
    @Query("{ 'members': { $all: ?0, $size: 2 } }")
    Optional<GroupModel> findGroupByTwoMembersExactMatch(List<String> memberIds);

}
