package com.main.dto;

public record CreateOrderItemRequest(
        Long productId,
        Integer quantity
) {
}