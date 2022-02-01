package com.zelazobeton.cognitiveexercisesmemory.services.impl;

import java.security.Principal;
import java.util.Optional;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

import com.zelazobeton.cognitiveexercisesmemory.domain.User;
import com.zelazobeton.cognitiveexercisesmemory.repository.UserRepository;
import com.zelazobeton.cognitiveexercisesmemory.services.UserService;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUserOrCreateNewOne(Principal principal) {
        Optional<User> optionalUser = this.userRepository.findUserByUsername(principal.getName());
        return optionalUser.orElseGet(() -> this.createUser(principal));
    }

    private User createUser(Principal principal) {
        String externalId = this.getPrincipalExternalId(principal);
        return User.builder().username(principal.getName()).externalId(externalId).build();
    }

    @Override
    public String getPrincipalExternalId(Principal principal) {
        Object keycloakPrincipalObject = ((KeycloakAuthenticationToken) principal).getPrincipal();
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) keycloakPrincipalObject;
        return keycloakPrincipal.getKeycloakSecurityContext().getToken().getSubject();
    }
}
