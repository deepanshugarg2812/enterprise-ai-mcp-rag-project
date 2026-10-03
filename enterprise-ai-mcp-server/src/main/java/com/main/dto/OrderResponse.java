package com.main.dto;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        String status,
        Long totalAmountPaise,
        Instant createdAt,
        List<OrderItemResponse> items
) {
}