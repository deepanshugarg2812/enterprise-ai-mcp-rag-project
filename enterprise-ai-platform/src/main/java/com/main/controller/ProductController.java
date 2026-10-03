package com.main.controller;

import com.main.dto.CreateProductRequest;
import com.main.dto.ProductResponse;
import com.main.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @Valid @RequestBody CreateProductRequest request
    ) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(
            @PathVariable Long id
    ) {
        return service.getById(id);
    }
}