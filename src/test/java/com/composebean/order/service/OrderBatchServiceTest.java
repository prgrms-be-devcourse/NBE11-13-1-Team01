package com.composebean.order.service;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.repository.OrderItemRepository;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/*@SpringBootTest
@Transactional // 테스트 후 DB 롤백*/
@ExtendWith(MockitoExtension.class)
class OrderBatchServiceTest {

    /*@Autowired
    private OrderBatchService orderBatchService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;*/

    //예외 처리용

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderBatchService orderBatchService;

    @Test
    @DisplayName("주문 병합 중 예외 발생 시 ORDER_MERGE_FAILED 예외가 발생한다")
    void autoGroupOrders_fail_mergeException() {
        // given

        // 1. 가짜 주문 데이터 2개 생성 (이메일과 주소를 똑같이 맞춰서 그룹핑 대상이 되게 함)
        Order order1 = Order.builder().email("test@test.com").address("서울").paymentStatus(PaymentStatus.PENDING).build();
        Order order2 = Order.builder().email("test@test.com").address("서울").paymentStatus(PaymentStatus.PENDING).build();
        List<Order> mockOrders = List.of(order1, order2);

        // 2. 리포지토리가 조회 메서드 호출 시 위 리스트를 반환하도록 설정
        when(orderRepository.findByOrderedAtBetweenAndPaymentStatus(any(), any(), any()))
                .thenReturn(mockOrders);

        // 3. 조회 시 병합 대상이 되는 주문 2개 이상이 리턴되도록 설정 (필요시 Mock 설정)
        doThrow(new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR))
                .when(orderRepository).delete(any(Order.class));

        // when & then

        // autoGroupOrders 메서드를 실행했을 때 BusinessException이 터지는지 검증
        assertThatThrownBy(() -> orderBatchService.autoGroupOrders())
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException ex = (BusinessException) exception;
                    // 에러 코드가 INTERNAL_SERVER_ERROR (500) 인지 확인
                    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR);
                });
    }

    @Test
    @DisplayName("주문 병합 시 수량과 소계(subtotal), 총합(totalPrice) 등이 정확히 계산되어야 한다")
    void mergeOrdersTest2() {
        // 1. Given: 상품 생성
        Product product = new Product("사과", 1000L, null, null, 345); // 가격 1000원
        Product product2 = new Product("배", 300L, null, null, 345); // 가격 1000원
        productRepository.save(product);
        productRepository.save(product2);

        // 주문 2개 생성 (같은 이메일, 주소)
        Order order1 = createOrder("user@test.com", "경기");
        order1.addOrderItem(createOrderItem(order1, product, 1)); // 1개 (subtotal: 1000)

        Order order2 = createOrder("user@test.com", "경기");
        order2.addOrderItem(createOrderItem(order2, product, 2)); // 2개 (subtotal: 2000)

        Order order3 = createOrder("user@test.com", "경기");
        order3.addOrderItem(createOrderItem(order3, product2, 5)); // 2개 (subtotal: 2000)

        Order order4 = createOrder("user@test2.com", "경기");
        order4.addOrderItem(createOrderItem(order4, product2, 2)); // 2개 (subtotal: 2000)

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
        orderRepository.save(order4);

        // 2. When: 배치 실행
        orderBatchService.autoGroupOrders();

        // 3. Then: 검증
        List<Order> results = orderRepository.findAll();
        assertThat(results).hasSize(5);

        Order mergedOrder = results.get(0);
        Order mergedOrder2 = results.get(1);

        OrderItem item = mergedOrder.getOrderItems().get(0);
        OrderItem item2 = mergedOrder.getOrderItems().get(1);

        //배송상태 변경 확인
        assertThat(mergedOrder.getDeliveryStatus().equals(DeliveryStatus.SHIPPING));
        assertThat(mergedOrder2.getDeliveryStatus().equals(DeliveryStatus.SHIPPING));

        //결제상태 변경 확인
        assertThat(mergedOrder.getPaymentStatus().equals(PaymentStatus.PAID));
        assertThat(mergedOrder2.getPaymentStatus().equals(PaymentStatus.PAID));

        //배송예정일 확인
        assertThat(mergedOrder.getDeliveryExpectedDate().getDayOfMonth()).isEqualTo(21);

        // 아이템은 2개, 1개여야 함 (병합됨)
        assertThat(mergedOrder.getOrderItems()).hasSize(2);
        assertThat(mergedOrder2.getOrderItems()).hasSize(1);

        // 아이템 수량 및 필드 확인
        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item2.getQuantity()).isEqualTo(3);

        assertThat(item.getProduct().getName().equals("배"));
        assertThat(item2.getProduct().getName().equals("사과"));

        // 각 주문 소계 확인
        assertThat(item.getSubtotal()).isEqualTo(1500L);
        assertThat(item2.getSubtotal()).isEqualTo(3000L);

        // total 확인
        assertThat(mergedOrder.getTotalPrice()).isEqualTo(4500L);
        assertThat(mergedOrder2.getTotalPrice()).isEqualTo(600L);
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