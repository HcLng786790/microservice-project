package com.huuduc.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Integer id,
        Integer userId,
        String orderNumber,
        BigDecimal totalPrice,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        List<OrderLineResponse> orderLineResponses
) {
}
