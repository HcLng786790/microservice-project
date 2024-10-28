package com.huuduc.authservice.service;

import com.huuduc.authservice.dto.UserResponse;
import com.huuduc.authservice.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    List<User> getAll();

    User getUser();
}
