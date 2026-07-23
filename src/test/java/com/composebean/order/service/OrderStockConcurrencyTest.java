package com.composebean.order.service;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
import com.composebean.order.dto.OrderCreateRequest;
import com.composebean.order.dto.OrderItemRequest;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:order-stock-lock;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "slack.bot-token=",
        "slack.order-receiver-id="
})
class OrderStockConcurrencyTest {

    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 같은 상품을 주문하면 재고 수량만큼만 주문된다")
    void createOrderWithPessimisticLock() throws InterruptedException {
        Product product = productRepository.saveAndFlush(
                Product.builder()
                        .name("Colombia Narino")
                        .price(5000L)
                        .description("콜롬비아 원두")
                        .imageUrl("https://example.com/colombia.png")
                        .stockQuantity(1)
                        .build()
        );

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        Runnable orderTask = () -> {
            try {
                orderCreateService.createOrder(
                        createRequest(product.getId()),
                        LocalDateTime.now().plusDays(1)
                );
                successCount.incrementAndGet();
            } catch (BusinessException exception) {
                if (exception.getErrorCode() == ErrorCode.INSUFFICIENT_STOCK) {
                    failCount.incrementAndGet();
                }
            }
        };

        // 테스트 스레드 구성 참고: https://pixx.tistory.com/351
        Thread thread1 = new Thread(orderTask);
        Thread thread2 = new Thread(orderTask);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        Product savedProduct = productRepository.findById(product.getId())
                .orElseThrow();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(savedProduct.getStockQuantity()).isZero();
    }

    private OrderCreateRequest createRequest(Long productId) {
        return new OrderCreateRequest(
                "customer@example.com",
                "서울특별시 강남구 테헤란로 123",
                "06234",
                List.of(new OrderItemRequest(productId, 1))
        );
    }
}
