package com.zelazobeton.cognitiveexercisesmemory.model;

import com.zelazobeton.cognitiveexercisesmemory.domain.MemoryTile;

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
public class MemoryTileDto {
    private String imgAddress;
    private String memoryId;
    private boolean uncovered;

    public MemoryTileDto(MemoryTile memoryTile) {
        this.imgAddress = memoryTile.getMemoryImg().getAddress();
        this.memoryId = memoryTile.getMemoryImgId();
        this.uncovered = memoryTile.isUncovered();
    }
}
