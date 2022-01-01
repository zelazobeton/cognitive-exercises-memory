package com.zelazobeton.cognitiveexercisesmemory.model;

import java.util.List;
import java.util.stream.Collectors;

import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryBoard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryBoardDto {
    private List<MemoryTileDto> memoryTiles;
    private int numOfUncoveredTiles;

    public MemoryBoardDto(MemoryBoard memoryBoard) {
        this.numOfUncoveredTiles = memoryBoard.getNumOfUncoveredTiles();
        this.memoryTiles = memoryBoard.getMemoryTiles().stream().map(MemoryTileDto::new).collect(Collectors.toList());
    }
}
