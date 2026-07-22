package com.composebean.product.repository;

import com.composebean.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findAllByDeletedAtIsNull();

    List<Product>
    findByNameContainingIgnoreCaseAndDeletedAtIsNull(
            String name
    );

    Optional<Product> findByIdAndDeletedAtIsNull(
            Long productId
    );
}