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
@AllArgsConstructor
@NoArgsConstructor
@Document("users")
public class User extends BaseEntity {
    private String externalId;
    private String username;
    private MemoryBoard memoryBoard;
}
