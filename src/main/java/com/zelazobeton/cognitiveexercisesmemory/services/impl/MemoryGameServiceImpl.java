package com.zelazobeton.cognitiveexercisesmemory.services.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercisesmemory.constants.MemoryDiffLvl;
import com.zelazobeton.cognitiveexercisesmemory.domain.User;
import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryBoard;
import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryImg;
import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryTile;
import com.zelazobeton.cognitiveexercisesmemory.domain.messages.SaveScoreMessage;
import com.zelazobeton.cognitiveexercisesmemory.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercisesmemory.model.MemoryBoardDto;
import com.zelazobeton.cognitiveexercisesmemory.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercisesmemory.repository.UserRepository;
import com.zelazobeton.cognitiveexercisesmemory.services.MemoryGameService;
import com.zelazobeton.cognitiveexercisesmemory.services.UserService;
import com.zelazobeton.cognitiveexercisesmemory.services.rabbitMQ.MessagePublisherService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemoryGameServiceImpl implements MemoryGameService {
    private MemoryImgRepository memoryImgRepository;
    private UserRepository userRepository;
    private MessagePublisherService messagePublisherService;
    private UserService userService;

    public MemoryGameServiceImpl(MemoryImgRepository memoryImgRepository, UserRepository userRepository,
            MessagePublisherService messagePublisherService, UserService userService) {
        this.memoryImgRepository = memoryImgRepository;
        this.userRepository = userRepository;
        this.messagePublisherService = messagePublisherService;
        this.userService = userService;
    }

    @Override
    public MemoryBoardDto getSavedMemoryBoardDto(String username) throws UserNotFoundException {
        Optional<User> user = this.userRepository.findUserByUsername(username);
        if (user.isEmpty()) {
            return null;
        }
        MemoryBoard savedMemoryBoard = user.get().getMemoryBoard();
        if (savedMemoryBoard == null) {
            return null;
        }
        return new MemoryBoardDto(savedMemoryBoard);
    }

    @Override
    public MemoryBoardDto getNewMemoryBoardDto(String difficultyLvl) {
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
        return new MemoryBoardDto(this.generateMemoryBoard(numOfDifferentImgsNeeded));
    }

    @Override
    public void saveGame(User user, MemoryBoardDto memoryBoardDto) {
        MemoryBoard newMemoryBoard = this.createMemoryBoardFromMemoryBoardDto(memoryBoardDto);
        user.setMemoryBoard(newMemoryBoard);
        this.userRepository.save(user);
    }

    @Override
    public int saveScore(Principal principal, MemoryBoardDto memoryBoardDto) {
        Integer score = this.calculateScore(memoryBoardDto);
        String userExternalId = this.userService.getPrincipalExternalId(principal);
        SaveScoreMessage saveScoreMessage = new SaveScoreMessage(userExternalId, score);
        this.messagePublisherService.publishMessage(saveScoreMessage);
        return score;
    }

    private Integer calculateScore(MemoryBoardDto memoryBoardDto) {
        int score = memoryBoardDto.getMemoryTiles().size() * 4 - memoryBoardDto.getNumOfUncoveredTiles() / 2;
        return Math.max(score, memoryBoardDto.getMemoryTiles().size());
    }

    private MemoryBoard createMemoryBoardFromMemoryBoardDto(MemoryBoardDto memoryBoardDto) {
        List<MemoryTile> tiles = memoryBoardDto.getMemoryTiles().stream().map(tile -> {
            MemoryImg img = this.memoryImgRepository.findByAddress(tile.getImgAddress()).orElseThrow(RuntimeException::new);
            return new MemoryTile(img, img.getId(), tile.isUncovered());
        }).collect(Collectors.toList());
        return MemoryBoard.builder()
                .memoryTiles(tiles)
                .numOfUncoveredTiles(memoryBoardDto.getNumOfUncoveredTiles())
                .build();
    }

    private MemoryBoard generateMemoryBoard(int numOfDifferentImgsNeeded) {
        List<MemoryTile> tiles = this.generateTiles(numOfDifferentImgsNeeded);
        return MemoryBoard.builder().memoryTiles(tiles).numOfUncoveredTiles(0).build();
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
