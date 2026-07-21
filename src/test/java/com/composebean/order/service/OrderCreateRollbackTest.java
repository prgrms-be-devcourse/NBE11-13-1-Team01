package com.composebean.order.service;

import com.composebean.order.dto.OrderCreateRequest;
import com.composebean.order.dto.OrderItemRequest;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderCreateRollbackTest {

    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private ProductRepository productRepository;

    private Long firstProductId;
    private Long secondProductId;

    @AfterEach
    void cleanUp() {
        if (firstProductId != null) {
            productRepository.deleteById(firstProductId);
        }
        if (secondProductId != null) {
            productRepository.deleteById(secondProductId);
        }
    }

    @Test
    @DisplayName("두 번째 상품에서 실패하면 첫 번째 상품의 재고도 원래대로 돌아간다")
    void rollbackStockWhenSecondProductFails() {
        Product firstProduct = saveProduct("Rollback First", 5000L, 10);
        Product secondProduct = saveProduct("Rollback Second", 6000L, 1);
        firstProductId = firstProduct.getId();
        secondProductId = secondProduct.getId();

        OrderCreateRequest request = new OrderCreateRequest(
                "customer@example.com",
                "서울특별시 강남구 테헤란로 123",
                "06234",
                List.of(
                        new OrderItemRequest(firstProductId, 2),
                        new OrderItemRequest(secondProductId, 2)
                )
        );

        assertThatThrownBy(
                () -> orderCreateService.createOrder(
                        request,
                        LocalDateTime.now().plusDays(1)
                )
        ).isInstanceOf(IllegalArgumentException.class);

        Product savedFirstProduct = productRepository.findById(firstProductId)
                .orElseThrow();
        Product savedSecondProduct = productRepository.findById(secondProductId)
                .orElseThrow();

        assertThat(savedFirstProduct.getStockQuantity()).isEqualTo(10);
        assertThat(savedSecondProduct.getStockQuantity()).isEqualTo(1);
    }

    private Product saveProduct(String name, Long price, Integer stockQuantity) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .description(name + " 원두")
                .imageUrl("https://example.com/" + name)
                .stockQuantity(stockQuantity)
                .build();

        return productRepository.save(product);
    }
}
