package com.composebean.order.service;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void 배송_상태_변경_확인() {
        Order beforeUpdate = orderRepository.findById(orderId)
                .orElseThrow();

        assertThat(beforeUpdate.getDeliveryStatus())
                .isEqualTo(DeliveryStatus.PREPARING);

        DeliveryStatusUpdateRequest request =
                new DeliveryStatusUpdateRequest(DeliveryStatus.DELIVERED);

        orderUpdateService.updateDeliveryStatus(request, orderId);

        Order afterUpdate = orderRepository.findById(orderId)
                .orElseThrow();

        assertThat(afterUpdate.getDeliveryStatus())
                .isEqualTo(DeliveryStatus.DELIVERED);
    }

    @Test
    @DisplayName("존재하지 않는 주문의 배송 상태를 수정하면 예외가 발생한다")
    void 주문이_없으면_예외가_발생한다() {
        DeliveryStatusUpdateRequest request =
                new DeliveryStatusUpdateRequest(DeliveryStatus.DELIVERED);

        assertThatThrownBy(() ->
                orderUpdateService.updateDeliveryStatus(request, Long.MAX_VALUE)
        ).isInstanceOfSatisfying(
                BusinessException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.ORDER_NOT_FOUND)
        );
    }

    @Test
    @DisplayName("소프트 딜리트된 주문의 배송 상태를 수정하면 예외가 발생한다")
    void 삭제된_주문의_배송_상태는_수정할_수_없다() {
        Order deletedOrder = orderRepository.findById(orderId)
                .orElseThrow();

        deletedOrder.delete();
        orderRepository.flush();

        DeliveryStatusUpdateRequest request =
                new DeliveryStatusUpdateRequest(DeliveryStatus.DELIVERED);

        assertThatThrownBy(() ->
                orderUpdateService.updateDeliveryStatus(request, orderId)
        ).isInstanceOfSatisfying(
                BusinessException.class,
                exception -> assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.ORDER_NOT_FOUND)
        );

        assertThat(deletedOrder.getDeliveryStatus())
                .isEqualTo(DeliveryStatus.PREPARING);
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

    private OrderItem createOrderItem(
            Order order,
            Product product,
            int quantity
    ) {
        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .subtotal(product.getPrice() * quantity)
                .order(order)
                .build();

        return item;
    }
}