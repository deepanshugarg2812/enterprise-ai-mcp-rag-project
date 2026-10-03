package com.main.dto;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone
) {
}