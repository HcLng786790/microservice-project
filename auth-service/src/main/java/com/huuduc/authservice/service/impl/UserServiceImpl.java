package com.huuduc.authservice.service.impl;

import com.huuduc.authservice.dto.UserResponse;
import com.huuduc.authservice.model.User;
import com.huuduc.authservice.repository.UserRepository;
import com.huuduc.authservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {

        return this.userRepository.findAll();
    }

    @Override
    public User getUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User findUser = this.userRepository.findByUsername(authentication.getName())
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with username: " + authentication.getName())
                );

        return findUser;
    }
}
