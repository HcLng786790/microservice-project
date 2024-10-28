package com.huuduc.orderservice.dto;

import java.math.BigDecimal;

public record OrderLineRequest(
        Integer productId,
        Integer orderId,
        int quantity,
        BigDecimal price
) {
}
