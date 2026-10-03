package com.main.dto;

import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        OrderStatus status,
        Long totalAmountPaise,
        List<OrderItemResponse> items
) {

    public record OrderItemResponse(
            Long productId,
            String productName,
            int quantity,
            long priceAtPurchasePaise
    ) {}
}