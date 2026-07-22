package com.composebean.order.service;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderAutoUpdateTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderInquiryService orderInquiryService;

    @Test
    @Disabled("조회 시 배송 예정일보다 현재 시간이 더 크다면 자동 배송 완료 처리")
    void autoUpdateOrder() {
        // 1. Given: 상품 생성
        Product product = new Product("사과", 1000L, null, null, 345); // 가격 1000원
        productRepository.save(product);
        Order order1 = createOrder("user@test.com", "경기");
        order1.addOrderItem(createOrderItem(order1, product, 1));
        orderRepository.save(order1);
        Pageable pageable = PageRequest.of(0, 1000);
        orderInquiryService.getOrders("",pageable);

        //then
        assertThat(order1.getDeliveryStatus()).isEqualTo(DeliveryStatus.DELIVERED);
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
                .deliveryExpectedDate(LocalDateTime.of(
                        2026, 7, 21, 0, 0, 0))
                .orderedAt(LocalDateTime.now())
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
