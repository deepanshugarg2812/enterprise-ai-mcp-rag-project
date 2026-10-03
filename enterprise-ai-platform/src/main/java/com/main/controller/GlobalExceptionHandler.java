package com.main.controller;

import com.main.exceptions.ErrorResponse;
import com.main.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        return new ErrorResponse(
                OffsetDateTime.now(),
                404,
                ex.getCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        return new ErrorResponse(
                OffsetDateTime.now(),
                400,
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
    }
}