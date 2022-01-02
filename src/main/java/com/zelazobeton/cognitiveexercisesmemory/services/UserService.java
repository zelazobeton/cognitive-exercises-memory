package com.zelazobeton.cognitiveexercisesmemory.services;

import java.security.Principal;

import com.zelazobeton.cognitiveexercisesmemory.domain.User;

public interface UserService {
    User findUserOrCreateNewOne(Principal principal);
}
