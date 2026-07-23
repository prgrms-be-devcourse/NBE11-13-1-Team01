package com.composebean.order.service;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.repository.OrderItemRepository;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderInquiryTest {

    @Autowired
    private OrderBatchService orderBatchService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();

        orderItemRepository.flush();
        orderRepository.flush();
        productRepository.flush();
    }

    @Test
    @DisplayName("배치 처리된 원본 주문은 활성 주문 조회에서 제외된다")
    void excludeSoftDeletedOrderFromActiveOrders() {
        Product product = Product.builder()
                .name("사과")
                .price(1000L)
                .description("사과 상품")
                .imageUrl(null)
                .stockQuantity(345)
                .build();

        productRepository.save(product);

        Order pendingOrder = createPendingOrder(
                "user@test.com",
                "경기도 성남시"
        );
        pendingOrder.addOrderItem(createOrderItem(product, 1));

        Order paidOrder = createPaidOrder(
                "user@test.com",
                "경기도 성남시"
        );
        paidOrder.addOrderItem(createOrderItem(product, 2));

        orderRepository.save(pendingOrder);
        orderRepository.save(paidOrder);

        orderBatchService.autoGroupOrders();

        assertThat(orderRepository.findAllByDeletedAtIsNull())
                .hasSize(2);

        assertThat(paidOrder.getDeletedAt()).isNotNull();
        assertThat(pendingOrder.getDeletedAt()).isNull();
    }

    private Order createPendingOrder(
            String email,
            String address
    ) {
        return Order.builder()
                .email(email)
                .address(address)
                .postalCode("12345")
                .totalPrice(1000L)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDateTime.now().plusDays(1))
                .orderedAt(batchTargetTime())
                .build();
    }

    private Order createPaidOrder(
            String email,
            String address
    ) {
        return Order.builder()
                .email(email)
                .address(address)
                .postalCode("12345")
                .totalPrice(2000L)
                .paymentStatus(PaymentStatus.PAID)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDateTime.now().plusDays(1))
                .orderedAt(batchTargetTime())
                .build();
    }

    private OrderItem createOrderItem(
            Product product,
            int quantity
    ) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .subtotal(product.getPrice() * quantity)
                .build();
    }

    private LocalDateTime batchTargetTime() {
        return LocalDateTime.now()
                .minusDays(1)
                .withHour(15)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }
}