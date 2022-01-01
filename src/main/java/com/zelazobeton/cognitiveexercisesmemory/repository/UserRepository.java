package com.zelazobeton.cognitiveexercisesmemory.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zelazobeton.cognitiveexercisesmemory.domain.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByUsername(String username);
}
