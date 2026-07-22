package com.composebean.order.service;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.OrderCreateRequest;
import com.composebean.order.dto.OrderCreateResponse;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.dto.OrderItemRequest;
import com.composebean.order.exception.OrderNotFoundException;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.exception.ProductNotFoundException;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderCreateServiceTest {

    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailService orderDetailService;

    @Test
    @DisplayName("주문을 생성하면 금액을 계산하고 재고를 차감한다")
    void createOrder() {
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

        assertThat(response.getOrderId()).isNotNull();
        assertThat(response.getTotalPrice()).isEqualTo(16000L);
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(response.getDeliveryStatus())
                .isEqualTo(DeliveryStatus.PREPARING);
        assertThat(response.getDeliveryDate())
                .isEqualTo(deliveryExpectedDate.toLocalDate());

        Order savedOrder = orderRepository.findById(response.getOrderId()).orElseThrow();
        assertThat(savedOrder.getOrderItems()).hasSize(2);
        assertThat(savedOrder.getOrderItems().get(0).getSubtotal())
                .isEqualTo(10000L);
        assertThat(savedOrder.getOrderItems().get(1).getSubtotal())
                .isEqualTo(6000L);
        assertThat(productRepository.findById(colombia.getId())
                .orElseThrow()
                .getStockQuantity()).isEqualTo(98);
        assertThat(productRepository.findById(brazil.getId())
                .orElseThrow()
                .getStockQuantity()).isEqualTo(79);
    }

    @Test
    @DisplayName("주문 수량이 재고보다 많으면 주문할 수 없다")
    void createOrderWithInsufficientStock() {
        Product product = saveProduct("Colombia Narino", 5000L, 1);

        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(product.getId(), 2)
        ));

        assertThatThrownBy(() -> orderCreateService.createOrder(
                request,
                LocalDateTime.now().plusDays(1)
        ))
                .isInstanceOfSatisfying(
                        BusinessException.class,
                        exception -> {
                            assertThat(exception.getErrorCode())
                                    .isEqualTo(ErrorCode.INSUFFICIENT_STOCK);
                            assertThat(exception.getMessage())
                                    .isEqualTo("상품 재고가 부족합니다.");
                        }
                );

        assertThat(productRepository.findById(product.getId())
                .orElseThrow()
                .getStockQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("주문 수량이 0이면 주문할 수 없다")
    void createOrderWithZeroQuantity() {
        Product product = saveProduct("Colombia Narino", 5000L, 100);

        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(product.getId(), 0)
        ));

        assertThatThrownBy(() -> orderCreateService.createOrder(
                request,
                LocalDateTime.now().plusDays(1)
        ))
                .isInstanceOfSatisfying(
                        BusinessException.class,
                        exception -> {
                            assertThat(exception.getErrorCode())
                                    .isEqualTo(ErrorCode.INVALID_ORDER_QUANTITY);
                            assertThat(exception.getMessage())
                                    .isEqualTo("주문 수량은 1개 이상이어야 합니다.");
                        }
                );
    }

    @Test
    @DisplayName("존재하지 않는 상품은 주문할 수 없다")
    void createOrderWithProductNotFound() {
        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(Long.MAX_VALUE, 1)
        ));

        assertThatThrownBy(() -> orderCreateService.createOrder(
                request,
                LocalDateTime.now().plusDays(1)
        ))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("상품을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("생성한 주문의 상세 정보와 주문 상품을 조회한다")
    void getOrderDetail() {
        Product product = saveProduct("Ethiopia Sidamo", 6500L, 10);
        OrderCreateRequest request = createRequest(List.of(
                new OrderItemRequest(product.getId(), 2)
        ));

        OrderCreateResponse created = orderCreateService.createOrder(
                request,
                LocalDateTime.of(2026, 7, 24, 14, 0)
        );

        OrderDetailResponse detail = orderDetailService.getOrder(created.getOrderId());

        assertThat(detail.getId()).isEqualTo(created.getOrderId());
        assertThat(detail.getEmail()).isEqualTo("customer@example.com");
        assertThat(detail.getTotalPrice()).isEqualTo(13000L);
        assertThat(detail.getItems()).hasSize(1);
        assertThat(detail.getItems().get(0).getProductId())
                .isEqualTo(product.getId());
        assertThat(detail.getItems().get(0).getProductName())
                .isEqualTo("Ethiopia Sidamo");
        assertThat(detail.getItems().get(0).getUnitPrice())
                .isEqualTo(6500L);
        assertThat(detail.getItems().get(0).getSubtotal())
                .isEqualTo(13000L);
    }

    @Test
    @DisplayName("존재하지 않는 주문은 상세 조회할 수 없다")
    void getOrderDetailNotFound() {
        assertThatThrownBy(() -> orderDetailService.getOrder(Long.MAX_VALUE))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("주문을 찾을 수 없습니다.");
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
