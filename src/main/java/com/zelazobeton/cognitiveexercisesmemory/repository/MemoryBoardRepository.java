package com.zelazobeton.cognitiveexercisesmemory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zelazobeton.cognitiveexercisesmemory.domain.memory.MemoryBoard;

public interface MemoryBoardRepository extends JpaRepository<MemoryBoard, Long> {

}
