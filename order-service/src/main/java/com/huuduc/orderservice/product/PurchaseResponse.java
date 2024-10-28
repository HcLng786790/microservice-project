package com.huuduc.orderservice.product;

import java.math.BigDecimal;

public record PurchaseResponse(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        int quantity
) {
}
