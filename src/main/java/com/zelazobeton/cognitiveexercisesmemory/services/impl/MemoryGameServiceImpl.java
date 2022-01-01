package com.zelazobeton.cognitiveexercisesmemory.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercisesmemory.constants.MemoryDiffLvl;
import com.zelazobeton.cognitiveexercisesmemory.domain.User;
import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryBoard;
import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryImg;
import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryTile;
import com.zelazobeton.cognitiveexercisesmemory.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercisesmemory.model.MemoryBoardDto;
import com.zelazobeton.cognitiveexercisesmemory.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercisesmemory.repository.UserRepository;
import com.zelazobeton.cognitiveexercisesmemory.services.MemoryGameService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemoryGameServiceImpl implements MemoryGameService {
    private MemoryImgRepository memoryImgRepository;
    private UserRepository userRepository;

    public MemoryGameServiceImpl(MemoryImgRepository memoryImgRepository,
            UserRepository userRepository) {
        this.memoryImgRepository = memoryImgRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MemoryBoardDto getSavedMemoryBoardDto(String username) throws UserNotFoundException {
        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        MemoryBoard savedMemoryBoard = user.getMemoryBoard();
        if (savedMemoryBoard == null) {
            return null;
        }
        return new MemoryBoardDto(savedMemoryBoard);
    }

    @Override
    public MemoryBoardDto getNewMemoryBoardDto(String username, String difficultyLvl) {
        int numOfDifferentImgsNeeded;
        switch (difficultyLvl){
            case "0":
                numOfDifferentImgsNeeded = MemoryDiffLvl.EASY.numOfImgs;
                break;
            case "2":
                numOfDifferentImgsNeeded = MemoryDiffLvl.HARD.numOfImgs;
                break;
            default:
                numOfDifferentImgsNeeded = MemoryDiffLvl.MEDIUM.numOfImgs;
        }

        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        MemoryBoard savedMemoryBoard = user.getMemoryBoard();
        if (savedMemoryBoard != null) {
            savedMemoryBoard.setUser(null);
        }
        user.setMemoryBoard(this.generateMemoryBoard(numOfDifferentImgsNeeded));
        User savedUser = this.userRepository.save(user);
        return new MemoryBoardDto(savedUser.getMemoryBoard());
    }

    @Override
    public void saveGame(String username, MemoryBoardDto memoryBoardDto) {
        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        MemoryBoard savedMemoryBoard = user.getMemoryBoard();
        if (savedMemoryBoard != null) {
            savedMemoryBoard.setUser(null);
        }
        MemoryBoard newMemoryBoard = this.createMemoryBoardFromMemoryBoardDto(memoryBoardDto, user);
        user.setMemoryBoard(newMemoryBoard);
        this.userRepository.save(user);
    }

    @Override
    public int saveScore(String username, MemoryBoardDto memoryBoardDto) {
//        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
//        Portfolio portfolio = user.getPortfolio();
//        int score = this.calculateScore(memoryBoardDto);
//        portfolio.setTotalScore(portfolio.getTotalScore() + score);
//        this.portfolioRepository.save(portfolio);
//        return score;
        return 0;
    }

    private int calculateScore(MemoryBoardDto memoryBoardDto) {
        int score = memoryBoardDto.getMemoryTiles().size() * 4 - memoryBoardDto.getNumOfUncoveredTiles() / 2;
        return Math.max(score, memoryBoardDto.getMemoryTiles().size());
    }

    private MemoryBoard createMemoryBoardFromMemoryBoardDto(MemoryBoardDto memoryBoardDto, User user) {
        List<MemoryTile> tiles = memoryBoardDto.getMemoryTiles().stream().map(tile -> {
            MemoryImg img = this.memoryImgRepository.findByAddress(tile.getImgAddress()).orElseThrow(RuntimeException::new);
            return new MemoryTile(img, img.getId(), tile.isUncovered());
        }).collect(Collectors.toList());
        return MemoryBoard.builder()
                .user(user)
                .memoryTiles(tiles)
                .numOfUncoveredTiles(memoryBoardDto.getNumOfUncoveredTiles())
                .build();
    }

    private MemoryBoard generateMemoryBoard(int numOfDifferentImgsNeeded) {
        List<MemoryTile> tiles = this.generateTiles(numOfDifferentImgsNeeded);
        return MemoryBoard.builder().memoryTiles(tiles).numOfUncoveredTiles(0).user(null).build();
    }

    private List<MemoryTile> generateTiles(int numOfImgs) {
        Pageable topTwenty = PageRequest.of(0, numOfImgs);
        List<MemoryImg> imgs = this.memoryImgRepository.findAll(topTwenty).getContent();
        List<MemoryTile> tiles = new ArrayList<>();
        imgs.forEach(img -> {
            tiles.add(MemoryTile.builder().memoryImg(img).uncovered(false).memoryImgId(img.getId()).build());
            tiles.add(MemoryTile.builder().memoryImg(img).uncovered(false).memoryImgId(img.getId()).build());
        });
        Collections.shuffle(tiles);
        return tiles;
    }
}
