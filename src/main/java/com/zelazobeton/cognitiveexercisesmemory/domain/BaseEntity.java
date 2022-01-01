package com.zelazobeton.cognitiveexercisesmemory.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity implements Serializable {
    @Id
    protected String id;
}
