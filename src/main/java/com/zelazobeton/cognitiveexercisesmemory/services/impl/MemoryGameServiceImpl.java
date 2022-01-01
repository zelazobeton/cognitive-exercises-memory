package com.zelazobeton.cognitiveexercisesmemory.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercisesmemory.constants.MemoryDiffLvl;
import com.zelazobeton.cognitiveexercisesmemory.domain.Portfolio;
import com.zelazobeton.cognitiveexercisesmemory.domain.User;
import com.zelazobeton.cognitiveexercisesmemory.domain.memory.MemoryBoard;
import com.zelazobeton.cognitiveexercisesmemory.domain.memory.MemoryImg;
import com.zelazobeton.cognitiveexercisesmemory.domain.memory.MemoryTile;
import com.zelazobeton.cognitiveexercisesmemory.exception.UserNotFoundException;
import com.zelazobeton.cognitiveexercisesmemory.model.memory.MemoryBoardDto;
import com.zelazobeton.cognitiveexercisesmemory.repository.MemoryImgRepository;
import com.zelazobeton.cognitiveexercisesmemory.repository.PortfolioRepository;
import com.zelazobeton.cognitiveexercisesmemory.repository.UserRepository;
import com.zelazobeton.cognitiveexercisesmemory.services.MemoryGameService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MemoryGameServiceImpl implements MemoryGameService {
    private PortfolioRepository portfolioRepository;
    private MemoryImgRepository memoryImgRepository;
    private UserRepository userRepository;

    public MemoryGameServiceImpl(PortfolioRepository portfolioRepository, MemoryImgRepository memoryImgRepository,
            UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.memoryImgRepository = memoryImgRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MemoryBoardDto getSavedMemoryBoardDto(String username) throws UserNotFoundException {
        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        MemoryBoard savedMemoryBoard = user.getPortfolio().getMemoryBoard();
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
        Portfolio portfolio = user.getPortfolio();
        MemoryBoard savedMemoryBoard = portfolio.getMemoryBoard();
        if (savedMemoryBoard != null) {
            savedMemoryBoard.setPortfolio(null);
        }
        portfolio.setMemoryBoard(this.generateMemoryBoard(numOfDifferentImgsNeeded));
        Portfolio savedPortfolio = this.portfolioRepository.save(portfolio);
        return new MemoryBoardDto(savedPortfolio.getMemoryBoard());
    }

    @Override
    public void saveGame(String username, MemoryBoardDto memoryBoardDto) {
        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        Portfolio portfolio = user.getPortfolio();
        MemoryBoard savedMemoryBoard = portfolio.getMemoryBoard();
        if (savedMemoryBoard != null) {
            savedMemoryBoard.setPortfolio(null);
        }
        MemoryBoard newMemoryBoard = this.createMemoryBoardFromMemoryBoardDto(memoryBoardDto, portfolio);
        portfolio.setMemoryBoard(newMemoryBoard);
        this.portfolioRepository.save(portfolio);
    }

    @Override
    public int saveScore(String username, MemoryBoardDto memoryBoardDto) {
        User user = this.userRepository.findUserByUsername(username).orElseThrow(UserNotFoundException::new);
        Portfolio portfolio = user.getPortfolio();
        int score = this.calculateScore(memoryBoardDto);
        portfolio.setTotalScore(portfolio.getTotalScore() + score);
        this.portfolioRepository.save(portfolio);
        return score;
    }

    private int calculateScore(MemoryBoardDto memoryBoardDto) {
        int score = memoryBoardDto.getMemoryTiles().size() * 4 - memoryBoardDto.getNumOfUncoveredTiles() / 2;
        return Math.max(score, memoryBoardDto.getMemoryTiles().size());
    }

    private MemoryBoard createMemoryBoardFromMemoryBoardDto(MemoryBoardDto memoryBoardDto, Portfolio portfolio)
            throws EntityNotFoundException {
        List<MemoryTile> tiles = memoryBoardDto.getMemoryTiles().stream().map(tile -> {
            MemoryImg img = this.memoryImgRepository.findByAddress(tile.getImgAddress()).orElseThrow(EntityNotFoundException::new);
            return new MemoryTile(img, img.getId(), tile.isUncovered());
        }).collect(Collectors.toList());
        return MemoryBoard.builder()
                .portfolio(portfolio)
                .memoryTiles(tiles)
                .numOfUncoveredTiles(memoryBoardDto.getNumOfUncoveredTiles())
                .build();
    }

    private MemoryBoard generateMemoryBoard(int numOfDifferentImgsNeeded) {
        List<MemoryTile> tiles = this.generateTiles(numOfDifferentImgsNeeded);
        return MemoryBoard.builder().memoryTiles(tiles).numOfUncoveredTiles(0).portfolio(null).build();
    }

    private List<MemoryTile> generateTiles(int numOfImgs) {
        Pageable topTwenty = PageRequest.of(0, numOfImgs);
        List<MemoryImg> imgs = this.memoryImgRepository.findAll(topTwenty).getContent();
        List<MemoryTile> tiles = new ArrayList<>();
        imgs.forEach(img -> {
            tiles.add(MemoryTile.builder().memoryImg(img).uncovered(false).memory_img_id(img.getId()).build());
            tiles.add(MemoryTile.builder().memoryImg(img).uncovered(false).memory_img_id(img.getId()).build());
        });
        Collections.shuffle(tiles);
        return tiles;
    }
}
