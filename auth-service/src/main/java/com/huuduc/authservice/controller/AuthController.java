package com.huuduc.authservice.controller;

import com.huuduc.authservice.dto.*;
import com.huuduc.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Đăng kí người dùng mới
     *
     * @param userRequest: username, password
     * @return userResponse: id,username
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @RequestBody @Valid UserRequest userRequest
    ){

        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED, null, this.authService.register(userRequest)
        );

        log.info("Accessed /register");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Đăng nhập người dùng
     *
     * @param request: username, password
     * @return AuthResponse: accessToken, refreshToken
     */
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(
            @RequestBody @Valid AuthRequest request
    ) {
        ApiResponse<AuthResponse> apiResponse = new ApiResponse<>(
                HttpStatus.CREATED, null, this.authService.authenticate(request)
        );

        log.info("Accessed /authenticate");
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Xác thực token (Dùng cho các service khác khi cần xác thực)
     *
     * @param verifyRequest: token
     * @return verifyResponse: boolean isValid
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyResponse>> verifyToken(
            @RequestBody @Valid VerifyRequest verifyRequest
    ) {

        ApiResponse<VerifyResponse> apiResponse = new ApiResponse<>(
                HttpStatus.OK, null, this.authService.verify(verifyRequest)
        );

        log.info("Accessed /verify");
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Làm mới token khi token hết hạn
     *
     * @param request
     * @param response
     * @return authResponse: accessToken, refreshToken
     * @throws IOException
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        this.authService.refreshToken(request,response);
        return null;
    }
}
