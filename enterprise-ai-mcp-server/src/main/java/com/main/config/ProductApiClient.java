package com.main.config;

import com.main.dto.ProductPage;
import com.main.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductApiClient {

    private final RestClient restClient;

    public ProductApiClient(
            RestClient.Builder builder,
            @Value("${api.base-url}") String baseUrl) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public ProductResponse getProduct(Long productId) {

        return restClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .body(ProductResponse.class);
    }

    public ProductPage searchProducts(
            String query,
            int page,
            int size) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products")
                        .queryParam("search", query)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(ProductPage.class);
    }
}