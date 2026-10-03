package com.main.controller;


import com.main.dto.CreateOrderRequest;
import com.main.dto.OrderResponse;
import com.main.dto.OrderStatus;
import com.main.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(
            @PathVariable Long id
    ) {
        return service.getById(id);
    }

    @GetMapping
    public Page<OrderResponse> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long customerId,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return service.findOrders(
                status,
                customerId,
                pageable
        );
    }
}