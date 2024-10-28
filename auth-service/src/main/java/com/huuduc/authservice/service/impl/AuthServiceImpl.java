package com.huuduc.authservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huuduc.authservice.dto.UserRequest;
import com.huuduc.authservice.dto.*;
import com.huuduc.authservice.exception.JwtException;
import com.huuduc.authservice.exception.UsernameAlreadyExistsException;
import com.huuduc.authservice.mapper.UserMapper;
import com.huuduc.authservice.model.User;
import com.huuduc.authservice.repository.RoleRepository;
import com.huuduc.authservice.repository.UserRepository;
import com.huuduc.authservice.security.jwt.JwtService;
import com.huuduc.authservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MessageSource messageSource;

    @Override
    public UserResponse register(UserRequest userRequest) {

        // Kiểm tra nếu username đã tồn tại
        if (this.userRepository.findByUsername(userRequest.username()).isPresent()) {

            throw new UsernameAlreadyExistsException(
                    this.messageSource.getMessage("conflict.username", null, LocaleContextHolder.getLocale())
            );
        }

        // Tìm kiếm quyền USER trong database
        var findRole = roleRepository.findByName("USER")
                .orElseThrow(
                        () -> new IllegalStateException(messageSource.getMessage("notfound.role", null, LocaleContextHolder.getLocale()))
                );

        // Tạo ra user mới và mã hóa mật khẩu
        var newUser = User.builder()
                .username(userRequest.username())
                .password(this.passwordEncoder.encode(userRequest.password()))
                .build();

        // Gán quyền USER cho người dùng mới
        newUser.setRoles(List.of(findRole));

        // Lưu lại
        this.userRepository.save(newUser);


        // Trả về kết quả
        return this.userMapper.fromUser(newUser);
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {

        // Xác thực thông tin đăng nhập từ request (username, password)
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // Lấy đối tượng User từ thông tin xác thực thành công
        User user = (User) authentication.getPrincipal();

        // Tạo JWT token cho user đã được xác thực
        String token = this.jwtService.generateToken(user);
        log.info("Access token: {}", token);

        // Tạo JWT refresh token cho user đã xác thực
        String refreshToken = this.jwtService.generateRefreshToken(user);
        log.info("Refresh token: {}", refreshToken);

        // Trả về đối tượng AuthResponse gồm token và trạng thái thành công
        return new AuthResponse(
                token,
                true,
                refreshToken
        );
    }

    @Override
    public VerifyResponse verify(VerifyRequest verifyRequest) {

        // Lấy token ra từ yêu cầu
        var token = verifyRequest.token();
        log.info("Token is:{}", token);

        // Kiểm tra tính hợp lệ của token
        try {
            return new VerifyResponse(jwtService.tokenValid(token));
        } catch (Exception e) {

            throw new JwtException((messageSource.getMessage("exception.token", null, LocaleContextHolder.getLocale())));
        }
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Lấy ra thông tin xác thực từ header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        username = this.jwtService.extractUsername(refreshToken);

        if (username != null) {
            var userDetails = this.userRepository.findByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken,userDetails)){

                var accessToken = this.jwtService.generateToken(userDetails);
                var authResponse = new AuthResponse(
                        accessToken,
                        true,
                        refreshToken
                );
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
