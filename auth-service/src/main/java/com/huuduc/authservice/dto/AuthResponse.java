package com.huuduc.authservice.dto;

public record AuthResponse(
        String token,
        boolean authenticated,
        String refreshToken
) {
}
