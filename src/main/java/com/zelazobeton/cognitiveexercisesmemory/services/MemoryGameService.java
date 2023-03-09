package com.zelazobeton.cognitiveexercisesmemory.services;

import java.security.Principal;

import com.zelazobeton.cognitiveexercisesmemory.domain.User;
import com.zelazobeton.cognitiveexercisesmemory.model.MemoryBoardDto;

public interface MemoryGameService {
    MemoryBoardDto getSavedMemoryBoardDto(String username);
    MemoryBoardDto getNewMemoryBoardDto(String difficultyLvl);
    void saveGame(User user, MemoryBoardDto memoryBoardDto) throws InterruptedException;
    int saveScore(Principal principal, MemoryBoardDto memoryBoardDto);
}
