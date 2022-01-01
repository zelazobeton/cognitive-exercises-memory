package com.zelazobeton.cognitiveexercisesmemory.services;

import com.zelazobeton.cognitiveexercisesmemory.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercisesmemory.model.MemoryBoardDto;

public interface MemoryGameService {
    MemoryBoardDto getSavedMemoryBoardDto(String username) throws UserNotFoundException;
    MemoryBoardDto getNewMemoryBoardDto(String username, String difficultyLvl);
    void saveGame(String username, MemoryBoardDto memoryBoardDto);
    int saveScore(String username, MemoryBoardDto memoryBoardDto);
}
