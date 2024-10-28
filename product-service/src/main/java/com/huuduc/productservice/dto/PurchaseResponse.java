package com.huuduc.productservice.dto;

import java.math.BigDecimal;

public record PurchaseResponse(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        int quantity
) {
}
