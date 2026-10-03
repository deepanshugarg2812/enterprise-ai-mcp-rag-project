package com.main.repository;

import com.main.dto.OrderStatus;
import com.main.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCustomerId(
            Long customerId,
            Pageable pageable
    );

    Page<Order> findByStatus(
            OrderStatus status,
            Pageable pageable
    );
}