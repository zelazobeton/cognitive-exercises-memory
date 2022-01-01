package com.zelazobeton.cognitiveexercisesmemory.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("memoryImgs")
public class MemoryImg extends BaseEntity {
    private String address;
}
