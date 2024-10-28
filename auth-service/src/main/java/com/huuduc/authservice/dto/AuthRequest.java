package com.huuduc.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record AuthRequest(

        @NotEmpty(message = "{username.mandatory}")
        @NotBlank(message = "{username.mandatory}")
        String username,

        @NotEmpty(message = "Password is mandatory")
        @NotBlank(message = "Password is mandatory")
        String password
) {
}
