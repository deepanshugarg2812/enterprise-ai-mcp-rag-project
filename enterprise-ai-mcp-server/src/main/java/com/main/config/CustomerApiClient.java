package com.main.config;

import com.main.dto.CustomerOrderPage;
import com.main.dto.CustomerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CustomerApiClient {
    private final RestClient restClient;

    public CustomerApiClient(
            RestClient.Builder builder,
            @Value("${api.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public CustomerResponse getCustomer(Long customerId) {

        return restClient.get()
                .uri("/api/customers/{id}", customerId)
                .retrieve()
                .body(CustomerResponse.class);
    }

    public CustomerOrderPage getCustomerOrders(
            Long customerId,
            int page,
            int size) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/orders")
                        .queryParam("customerId", customerId)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(CustomerOrderPage.class);
    }
}
