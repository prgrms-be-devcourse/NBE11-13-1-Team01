package com.composebean.order.service;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
public class OrderInquiryTest {

    @Autowired
    private OrderBatchService orderBatchService;

    @Autowired
    private OrderInquiryService orderInquiryService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 소프트_딜리트시_안보이는지_확인() {
        // 1. Given: 상품 생성
        Product product = new Product("사과", 1000L, null, null, 345); // 가격 1000원
        productRepository.save(product);

        // 주문 2개 생성 (같은 이메일, 주소)
        Order order1 = createOrder("user@test.com", "경기");
        order1.addOrderItem(createOrderItem(order1, product, 1)); // 1개 (subtotal: 1000)

        Order order2 = createOrder2("user@test.com", "경기");
        order2.addOrderItem(createOrderItem(order2, product, 2)); // 2개 (subtotal: 2000)


        orderRepository.save(order1);
        orderRepository.save(order2);

        // 2. When: 배치 실행
        orderBatchService.autoGroupOrders();

        assertThat(orderRepository.findAllByDeletedAtIsNull().size()).isEqualTo(3);

        assertThat(order2.getDeletedAt()).isNotNull();

        assertThat(order1.getDeletedAt()).isNull();
    }


    // 테스트용 헬퍼 메서드
    private Order createOrder(String email, String address) {
        Order order = Order.builder()
                .email(email)
                .address(address)
                .postalCode("234-323")
                .totalPrice(33434L)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDateTime.now())
                .orderedAt(LocalDateTime.of(2026,1,1,1,1))
                .build();
        return order;
    }

    private Order createOrder2(String email, String address) {
        Order order = Order.builder()
                .email(email)
                .address(address)
                .postalCode("234-323")
                .totalPrice(33434L)
                .paymentStatus(PaymentStatus.PAID)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDateTime.now())
                .orderedAt(LocalDateTime.of(2026,7,21,17,1))
                .build();
        return order;
    }

    private OrderItem createOrderItem(Order order, Product product, int quantity) {
        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .subtotal(product.getPrice()*quantity)
                .order(order)
                .build();

        return item;
    }
}
