package com.huuduc.orderservice.dto;

import com.huuduc.orderservice.product.PurchaseResponse;
import com.huuduc.orderservice.user.UserResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderNumber,
        BigDecimal totalPrice,
        UserResponse userResponse,
        List<PurchaseResponse> purchaseResponseList
) {
}
