package com.huuduc.productservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        int stock,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
}
