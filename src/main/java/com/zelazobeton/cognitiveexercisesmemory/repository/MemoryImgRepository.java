package com.zelazobeton.cognitiveexercisesmemory.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryImg;

@Repository
public interface MemoryImgRepository extends MongoRepository<MemoryImg, String> {
    Optional<MemoryImg> findByAddress(String address);
}
