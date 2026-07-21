package com.composebean.order.service;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.DeliveryStatusUpdateRequest;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 가짜가 아닌 진짜 스프링 컨테이너와 DB 띄우기
@Transactional // 테스트 후 DB 롤백
class OrderUpdateServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderUpdateService orderUpdateService;

    Long orderId;

    @BeforeEach
    void setUp() {
        // 1. Given: 상품 생성
        Product product = new Product("사과", 1000L, null, null, 345); // 가격 1000원
        productRepository.save(product);

        // 주문 1개
        Order order1 = createOrder("user@test.com", "경기");
        order1.addOrderItem(createOrderItem(order1, product, 1)); // 1개 (subtotal: 1000)
        orderRepository.save(order1);
        orderId = order1.getId();
    }

    @Test
    @DisplayName("배송 상태 변경 확인")
    void 배송_상태_변경_확인(){
        assertThat(orderRepository.findById(orderId).get().getDeliveryStatus()).isEqualTo(DeliveryStatus.PREPARING);
        DeliveryStatusUpdateRequest request = new DeliveryStatusUpdateRequest(DeliveryStatus.DELIVERED);
        orderUpdateService.updateDeliveryStatus(request, orderId);
        assertThat(orderRepository.findById(orderId).get().getDeliveryStatus()).isEqualTo(DeliveryStatus.DELIVERED);
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