package com.huuduc.notificationservice.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderNumber,
        BigDecimal totalPrice,
        UserResponse userResponse,
        List<PurchaseProduct> purchaseResponseList
) {
}
