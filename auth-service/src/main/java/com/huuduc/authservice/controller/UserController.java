package com.huuduc.authservice.controller;

import com.huuduc.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
@SuppressWarnings("unused")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Lấy toàn bộ thông tin user (Quyền ADMIN)
     *
     * @return Danh sách thông tin user
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {

        log.info("Accessed user/all");

        return ResponseEntity.ok(this.userService.getAll());
    }

    /**
     * Hiển thị thông tin chính mình hiện đang làm việc
     *
     * @return The info of user
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<?> getUser() {

        log.info("Accessed getUser");

        return ResponseEntity.ok(this.userService.getUser());
    }
}
