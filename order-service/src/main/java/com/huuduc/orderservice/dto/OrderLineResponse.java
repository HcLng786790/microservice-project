package com.huuduc.orderservice.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Integer id,
        Integer productId,
        int quantity,
        BigDecimal price
) {
}
