package com.huuduc.productservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(

        @NotNull(message = "Product name is required")
        String name,
        @NotNull(message = "Product description is required")
        String description,
        @Positive(message = "Stock should be positive")
        int stock,
        @Positive(message = "Price should be potitive")
        BigDecimal price
) {
}
