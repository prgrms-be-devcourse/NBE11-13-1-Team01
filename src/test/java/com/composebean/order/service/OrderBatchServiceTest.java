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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderBatchServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderBatchService orderBatchService;

    @Test
    @DisplayName("주문 병합 중 예외가 발생하면 INTERNAL_SERVER_ERROR 예외가 발생한다")
    void autoGroupOrdersFail() {
        OrderItem orderItem = mock(OrderItem.class);

        Order firstOrder = createOrder(
                "test@test.com",
                "서울특별시 강남구"
        );
        firstOrder.addOrderItem(orderItem);

        Order secondOrder = createOrder(
                "test@test.com",
                "서울특별시 강남구"
        );

        when(orderRepository.findByOrderedAtBetweenAndPaymentStatus(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(PaymentStatus.PAID)
        )).thenReturn(List.of(firstOrder, secondOrder));

        doThrow(new IllegalStateException("주문 상품 이동 실패"))
                .when(orderItem)
                .assignOrder(any(Order.class));

        assertThatThrownBy(orderBatchService::autoGroupOrders)
                .isInstanceOfSatisfying(
                        BusinessException.class,
                        exception -> assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR)
                );
    }

    @Test
    @DisplayName("같은 이메일과 주소의 주문을 상품별로 병합한다")
    void mergeOrders() {
        Product apple = mock(Product.class);
        when(apple.getId()).thenReturn(1L);
        when(apple.getName()).thenReturn("사과");
        when(apple.getPrice()).thenReturn(1000L);

        Product pear = mock(Product.class);
        when(pear.getId()).thenReturn(2L);
        when(pear.getName()).thenReturn("배");
        when(pear.getPrice()).thenReturn(300L);

        Order firstOrder = createOrder(
                "user@test.com",
                "경기도 성남시"
        );
        firstOrder.addOrderItem(createOrderItem(apple, 1));

        Order secondOrder = createOrder(
                "user@test.com",
                "경기도 성남시"
        );
        secondOrder.addOrderItem(createOrderItem(apple, 2));

        Order thirdOrder = createOrder(
                "user@test.com",
                "경기도 성남시"
        );
        thirdOrder.addOrderItem(createOrderItem(pear, 5));

        when(orderRepository.findByOrderedAtBetweenAndPaymentStatus(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(PaymentStatus.PAID)
        )).thenReturn(List.of(
                firstOrder,
                secondOrder,
                thirdOrder
        ));

        orderBatchService.autoGroupOrders();

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(orderCaptor.capture());

        Order mergedOrder = orderCaptor.getValue();

        assertThat(mergedOrder.getDeliveryStatus())
                .isEqualTo(DeliveryStatus.SHIPPING);
        assertThat(mergedOrder.getPaymentStatus())
                .isEqualTo(PaymentStatus.PAID);

        assertThat(mergedOrder.getOrderItems()).hasSize(2);
        assertThat(mergedOrder.getTotalPrice()).isEqualTo(4500L);

        OrderItem appleItem = mergedOrder.getOrderItems().stream()
                .filter(item ->
                        item.getProduct().getId().equals(apple.getId())
                )
                .findFirst()
                .orElseThrow();

        OrderItem pearItem = mergedOrder.getOrderItems().stream()
                .filter(item ->
                        item.getProduct().getId().equals(pear.getId())
                )
                .findFirst()
                .orElseThrow();

        assertThat(appleItem.getProduct().getName()).isEqualTo("사과");
        assertThat(appleItem.getQuantity()).isEqualTo(3);
        assertThat(appleItem.getUnitPrice()).isEqualTo(1000L);
        assertThat(appleItem.getSubtotal()).isEqualTo(3000L);

        assertThat(pearItem.getProduct().getName()).isEqualTo("배");
        assertThat(pearItem.getQuantity()).isEqualTo(5);
        assertThat(pearItem.getUnitPrice()).isEqualTo(300L);
        assertThat(pearItem.getSubtotal()).isEqualTo(1500L);

        assertThat(firstOrder.getDeletedAt()).isNotNull();
        assertThat(secondOrder.getDeletedAt()).isNotNull();
        assertThat(thirdOrder.getDeletedAt()).isNotNull();
    }

    private Order createOrder(String email, String address) {
        return Order.builder()
                .email(email)
                .address(address)
                .postalCode("12345")
                .totalPrice(0L)
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