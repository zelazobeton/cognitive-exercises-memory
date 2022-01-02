package com.zelazobeton.cognitiveexercisesmemory.services;

import com.zelazobeton.cognitiveexercisesmemory.domain.User;
import com.zelazobeton.cognitiveexercisesmemory.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercisesmemory.model.MemoryBoardDto;

public interface MemoryGameService {
    MemoryBoardDto getSavedMemoryBoardDto(String username) throws UserNotFoundException;
    MemoryBoardDto getNewMemoryBoardDto(String difficultyLvl);
    void saveGame(User user, MemoryBoardDto memoryBoardDto);
    int saveScore(String username, MemoryBoardDto memoryBoardDto);
}
