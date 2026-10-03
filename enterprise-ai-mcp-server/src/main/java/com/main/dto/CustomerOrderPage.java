package com.main.dto;

import java.util.List;

public record CustomerOrderPage(
        List<OrderResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}