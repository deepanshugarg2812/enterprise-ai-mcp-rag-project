package com.main.repository;

import com.main.entities.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p
        from Product p
        where p.id = :id
    """)
    Optional<Product> findByIdForUpdate(
            @Param("id") Long id
    );
}