package com.main.repository;

import com.main.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
}
