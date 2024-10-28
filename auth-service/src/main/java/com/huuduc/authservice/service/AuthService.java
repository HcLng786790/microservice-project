package com.huuduc.authservice.service;

import com.huuduc.authservice.dto.UserRequest;
import com.huuduc.authservice.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface AuthService {

    UserResponse register(@Valid UserRequest userRequest);

    AuthResponse authenticate(@Valid AuthRequest request);

    VerifyResponse verify(@Valid VerifyRequest verifyRequest);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
