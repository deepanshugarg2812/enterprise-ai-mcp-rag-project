package com.main.dto;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        Long pricePaise,
        Integer inventory
) {
}