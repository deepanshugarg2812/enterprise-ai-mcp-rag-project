package com.main.service;


import com.main.dto.CreateCustomerRequest;
import com.main.dto.CustomerResponse;
import com.main.entities.Customer;
import com.main.exceptions.ResourceNotFoundException;
import com.main.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {

        if (repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Customer email already exists"
            );
        }

        Customer customer = new Customer();

        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        customer = repository.save(customer);

        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {

        Customer customer = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "CUSTOMER_NOT_FOUND",
                                "Customer " + id + " was not found"
                        )
                );

        return toResponse(customer);
    }

    private CustomerResponse toResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }
}