package com.composebean.order.service;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.OrderCreateRequest;
import com.composebean.order.dto.OrderCreateResponse;
import com.composebean.order.dto.OrderItemRequest;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderCreateServiceTest {

    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 주문을_생성하면_금액을_계산하고_재고를_차감한다() {
        Product colombia = saveProduct("Colombia Narino", 5000L, 100);
        Product brazil = saveProduct("Brazil Serra Do Caparao", 6000L, 80);

        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(colombia.getId(), 2),
                new OrderItemRequest(brazil.getId(), 1)
        ));
        LocalDateTime deliveryExpectedDate = LocalDateTime.of(2026, 7, 21, 14, 0);

        OrderCreateResponse response = orderCreateService.createOrder(
                request,
                deliveryExpectedDate
        );

        assertNotNull(response.getOrderId());
        assertEquals(16000L, response.getTotalPrice());
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());
        assertEquals(DeliveryStatus.PREPARING, response.getDeliveryStatus());
        assertEquals(deliveryExpectedDate.toLocalDate(), response.getDeliveryDate());

        Order savedOrder = orderRepository.findById(response.getOrderId()).orElseThrow();
        assertEquals(2, savedOrder.getOrderItems().size());
        assertEquals(10000L, savedOrder.getOrderItems().get(0).getSubtotal());
        assertEquals(6000L, savedOrder.getOrderItems().get(1).getSubtotal());
        assertEquals(98, productRepository.findById(colombia.getId()).orElseThrow().getStockQuantity());
        assertEquals(79, productRepository.findById(brazil.getId()).orElseThrow().getStockQuantity());
    }

    @Test
    void 주문_수량이_재고보다_많으면_주문할_수_없다() {
        Product product = saveProduct("Colombia Narino", 5000L, 1);
        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(product.getId(), 2)
        ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderCreateService.createOrder(request, LocalDateTime.now().plusDays(1))
        );

        assertEquals("상품 재고가 부족합니다.", exception.getMessage());
        assertEquals(1, productRepository.findById(product.getId()).orElseThrow().getStockQuantity());
    }

    @Test
    void 주문_수량이_0이면_주문할_수_없다() {
        Product product = saveProduct("Colombia Narino", 5000L, 100);
        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(product.getId(), 0)
        ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderCreateService.createOrder(request, LocalDateTime.now().plusDays(1))
        );

        assertEquals("주문 수량은 1개 이상이어야 합니다.", exception.getMessage());
    }

    @Test
    void 이메일_형식이_올바르지_않으면_주문할_수_없다() {
        Product product = saveProduct("Colombia Narino", 5000L, 100);
        OrderCreateRequest request = new OrderCreateRequest(
                "wrong-email",
                "서울특별시 강남구 테헤란로 123",
                "06234",
                List.of(new OrderItemRequest(product.getId(), 1))
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderCreateService.createOrder(request, LocalDateTime.now().plusDays(1))
        );

        assertEquals("올바른 이메일을 입력해 주세요.", exception.getMessage());
    }

    @Test
    void 존재하지_않는_상품은_주문할_수_없다() {
        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(Long.MAX_VALUE, 1)
        ));

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> orderCreateService.createOrder(request, LocalDateTime.now().plusDays(1))
        );

        assertEquals("존재하지 않는 상품입니다.", exception.getMessage());
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

    private OrderCreateRequest createRequest(List<OrderItemRequest> items) {
        return new OrderCreateRequest(
                "customer@example.com",
                "서울특별시 강남구 테헤란로 123",
                "06234",
                items
        );
    }
}
