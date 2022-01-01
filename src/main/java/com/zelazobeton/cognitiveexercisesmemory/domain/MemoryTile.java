package com.zelazobeton.cognitiveexercisesmemory.domain;

import org.springframework.data.mongodb.core.mapping.Document;

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
@Document("memoryTiles")
public class MemoryTile extends BaseEntity {
    private MemoryImg memoryImg;
    private String memoryImgId;
    private boolean uncovered;
}
