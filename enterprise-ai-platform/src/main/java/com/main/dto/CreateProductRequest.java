package com.main.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(

        @NotBlank
        String sku,

        @NotBlank
        String name,

        String description,

        @PositiveOrZero
        long pricePaise,

        @Min(0)
        int inventory
) {}