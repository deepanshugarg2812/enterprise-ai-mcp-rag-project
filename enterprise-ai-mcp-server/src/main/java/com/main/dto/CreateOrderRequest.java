package com.main.dto;

import java.util.List;

public record CreateOrderRequest(
        Long customerId,
        List<CreateOrderItemRequest> items
) {
}