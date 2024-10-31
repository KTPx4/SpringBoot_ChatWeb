package com.Px4.ChatAPI.models.relation;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface GroupRepository extends MongoRepository<GroupModel, String> {
    // Custom query to find groups containing all member IDs in the given list
    // Query to find a group containing exactly two specific members, order-independent
    // Query to find a group containing exactly two specific members, order-independent

}
