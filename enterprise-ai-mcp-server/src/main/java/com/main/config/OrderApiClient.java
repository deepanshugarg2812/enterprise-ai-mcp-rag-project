package com.main.config;

import com.main.dto.CreateOrderRequest;
import com.main.dto.OrderPage;
import com.main.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OrderApiClient {

    private final RestClient restClient;

    public OrderApiClient(
            RestClient.Builder builder,
            @Value("${api.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public OrderResponse getOrder(Long orderId) {

        return restClient.get()
                .uri("/api/orders/{id}", orderId)
                .retrieve()
                .body(OrderResponse.class);
    }

    public OrderPage searchOrders(
            Long customerId,
            String status,
            int page,
            int size) {

        return restClient.get()
                .uri(uriBuilder -> {

                    var builder = uriBuilder
                            .path("/api/orders")
                            .queryParam("page", page)
                            .queryParam("size", size);

                    if (customerId != null) {
                        builder.queryParam(
                                "customerId",
                                customerId
                        );
                    }

                    if (status != null && !status.isBlank()) {
                        builder.queryParam(
                                "status",
                                status
                        );
                    }

                    return builder.build();
                })
                .retrieve()
                .body(OrderPage.class);
    }

    public OrderResponse createOrder(
            CreateOrderRequest request,
            String idempotencyKey) {

        return restClient.post()
                .uri("/api/orders")
                .header(
                        "Idempotency-Key",
                        idempotencyKey
                )
                .body(request)
                .retrieve()
                .body(OrderResponse.class);
    }

    public OrderResponse cancelOrder(
            Long orderId,
            String idempotencyKey) {

        return restClient.post()
                .uri("/api/orders/{id}/cancel", orderId)
                .header(
                        "Idempotency-Key",
                        idempotencyKey
                )
                .retrieve()
                .body(OrderResponse.class);
    }
}