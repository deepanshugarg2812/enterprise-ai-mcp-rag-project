package com.main.controller;

import com.main.dto.CreateCustomerRequest;
import com.main.dto.CustomerResponse;
import com.main.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(
            @Valid @RequestBody CreateCustomerRequest request
    ) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(
            @PathVariable Long id
    ) {
        return service.getById(id);
    }
}