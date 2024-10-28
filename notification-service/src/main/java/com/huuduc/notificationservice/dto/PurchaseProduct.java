package com.huuduc.notificationservice.dto;

import java.math.BigDecimal;

public record PurchaseProduct(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        int quantity
) {
}
