package com.huuduc.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserRequest(

        @NotEmpty(message = "User name is mandatory")
        @NotBlank(message = "User name is mandatory")
        String username,

        @NotEmpty(message = "Password is mandatory")
        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "Password should be 6 characters long minimum")
        String password
) {
}
