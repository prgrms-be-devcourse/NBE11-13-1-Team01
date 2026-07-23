package com.composebean.product.repository;

import com.composebean.product.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.deletedAt IS NULL")
    Optional<Product> findByIdForUpdate(
            @Param("productId") Long productId
    );
}