package com.main.service;


import com.main.dto.*;
import com.main.entities.*;
import com.main.exceptions.ResourceNotFoundException;
import com.main.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {

        Customer customer = customerRepository.findById(
                request.customerId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "CUSTOMER_NOT_FOUND",
                        "Customer " + request.customerId() + " was not found"
                )
        );

        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PLACED);

        long total = 0;

        for (CreateOrderRequest.OrderItemRequest itemRequest
                : request.items()) {

            Product product = productRepository
                    .findByIdForUpdate(itemRequest.productId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "PRODUCT_NOT_FOUND",
                                    "Product " + itemRequest.productId()
                                            + " was not found"
                            )
                    );

            if (product.getInventory() < itemRequest.quantity()) {
                throw new IllegalStateException(
                        "Insufficient inventory for product "
                                + product.getId()
                );
            }

            product.setInventory(
                    product.getInventory()
                            - itemRequest.quantity()
            );

            long itemTotal =
                    product.getPricePaise()
                            * itemRequest.quantity();

            total += itemTotal;

            OrderItem item = new OrderItem();

            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setPriceAtPurchasePaise(
                    product.getPricePaise()
            );

            order.addItem(item);
        }

        order.setTotalAmountPaise(total);

        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ORDER_NOT_FOUND",
                                "Order " + id + " was not found"
                        )
                );

        return toResponse(order);
    }

    private String generateOrderNumber() {

        return "ORD-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    private OrderResponse toResponse(Order order) {

        List<OrderResponse.OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item ->
                                new OrderResponse.OrderItemResponse(
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getQuantity(),
                                        item.getPriceAtPurchasePaise()
                                )
                        )
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getStatus(),
                order.getTotalAmountPaise(),
                items
        );
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findOrders(
            OrderStatus status,
            Long customerId,
            Pageable pageable
    ) {

        Page<Order> orders;

        if (customerId != null) {

            orders = orderRepository
                    .findByCustomerId(customerId, pageable);

        } else if (status != null) {

            orders = orderRepository
                    .findByStatus(status, pageable);

        } else {

            orders = orderRepository
                    .findAll(pageable);
        }

        return orders.map(this::toResponse);
    }
}