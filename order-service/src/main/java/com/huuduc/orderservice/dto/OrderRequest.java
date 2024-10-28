package com.huuduc.orderservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(

        @NotEmpty(message = "Should at least purchase one or more product")
        List<PurchaseRequest> purchaseRequestList
) {
}
