package com.main.exceptions;

import java.time.OffsetDateTime;

public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String code,
        String message,
        String path
) {}