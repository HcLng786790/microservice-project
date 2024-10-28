package com.huuduc.authservice.dto;

import jakarta.validation.constraints.NotNull;

public record VerifyRequest(

        @NotNull(message = "Token should be required")
        String token
) {
}
