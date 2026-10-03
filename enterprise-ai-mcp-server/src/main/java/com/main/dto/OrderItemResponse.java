package com.main.dto;

public record OrderItemResponse(
        Long id,
        Long productId,
        Integer quantity,
        Long priceAtPurchasePaise
) {
}