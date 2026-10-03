package com.main.service;

import com.main.dto.CreateProductRequest;
import com.main.dto.ProductResponse;
import com.main.entities.Product;
import com.main.exceptions.ResourceNotFoundException;
import com.main.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {

        if (repository.existsBySku(request.sku())) {
            throw new IllegalArgumentException(
                    "Product SKU already exists"
            );
        }

        Product product = new Product();

        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPricePaise(request.pricePaise());
        product.setInventory(request.inventory());

        return toResponse(repository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "PRODUCT_NOT_FOUND",
                                "Product " + id + " was not found"
                        )
                );

        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPricePaise(),
                product.getInventory()
        );
    }
}
