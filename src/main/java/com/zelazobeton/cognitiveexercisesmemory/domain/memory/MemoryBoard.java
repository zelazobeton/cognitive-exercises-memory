package com.zelazobeton.cognitiveexercisesmemory.domain.memory;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.zelazobeton.cognitiveexercisesmemory.domain.BaseEntity;
import com.zelazobeton.cognitiveexercisesmemory.domain.Portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Setter
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class MemoryBoard extends BaseEntity {
    private Integer numOfUncoveredTiles;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoryTile> memoryTiles;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Portfolio portfolio;
}
