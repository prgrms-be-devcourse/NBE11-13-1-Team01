package com.composebean.order.repository;

import com.composebean.global.config.QueryDSLConfig;
import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.dto.OrderItemResponse;
import com.composebean.order.dto.OrderSummaryResponse;
import com.composebean.product.domain.Product;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //h2 말고 mysql이용
@Import(QueryDSLConfig.class) // queryDSL을 이용한 테스트를 할려면 필수
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long savedOrderId;

    @BeforeEach
    void setUp() { //하드 코딩하지 말기;; 리팩토링 대상
        Order order1 = Order.builder()
                .email("test@naver.com")
                .address("경기")
                .postalCode("12345")
                .totalPrice(3900L)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDateTime.of
                        (2026, 7, 23, 0, 0, 0))
                .orderedAt(LocalDateTime.of
                        (2026, 6, 20, 0, 0, 0))
                .build();
        Order order2 = Order.builder()
                .email("test@naver.com")
                .address("경기")
                .postalCode("125")
                .totalPrice(5700L)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDateTime.of
                        (2026, 8, 23, 0, 0, 0))
                .orderedAt(LocalDateTime.of
                        (2026, 6, 21, 0, 0, 0))
                .build();
        Order order3 = Order.builder()
                .email("test2@naver.com")
                .address("경기")
                .postalCode("145")
                .totalPrice(56400L)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDateTime.of
                        (2026, 9, 23, 0, 0, 0))
                .orderedAt(LocalDateTime.of
                        (2026, 6, 22, 0, 0, 0))
                .build();
        Order order4 = Order.builder()
                .email("test2@naver.com")
                .address("경기")
                .postalCode("12345")
                .totalPrice(2300L)
                .paymentStatus(PaymentStatus.PENDING)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(LocalDateTime.of
                        (2026, 7, 27, 0, 0, 0))
                .orderedAt(LocalDateTime.of
                        (2026, 6, 23, 0, 0, 0))
                .build();


        Product product1 = Product.builder()
                .price(1000L)
                .name("사과")
                .description(null)
                .imageUrl(null)
                .stockQuantity(345).build();
        Product product2 = Product.builder()
                .price(300L)
                .name("배")
                .description(null)
                .imageUrl(null)
                .stockQuantity(345).build();
        productRepository.save(product1);
        productRepository.save(product2);

        OrderItem orderItem1 = OrderItem.builder()
                .order(order1)
                .product(product2)
                .quantity(3)
                .unitPrice(300L)
                .subtotal(900L)
                .build();
        OrderItem orderItem2 = OrderItem.builder()
                .order(order2)
                .product(product1)
                .quantity(3)
                .unitPrice(1000L)
                .subtotal(3000L)
                .build();

        order1.addOrderItem(orderItem1);
        order2.addOrderItem(orderItem2);

        orderRepository.save(order1);
        this.savedOrderId = order1.getId();
        orderRepository.save(order2);
        orderRepository.save(order3);
        orderRepository.save(order4);
    }

    @Test
    @DisplayName("값 확인")
    void 값_확인() {

        Optional<OrderDetailResponse> response = orderRepository.getOrderDetail(savedOrderId);
        assertThat(response).isNotNull();
        OrderDetailResponse orderDetailResponse = response.get();
        assertThat(orderDetailResponse.getEmail()).isEqualTo("test@naver.com");
        assertThat(orderDetailResponse.getAddress()).isEqualTo("경기");
        assertThat(orderDetailResponse.getPostalCode()).isEqualTo("12345");
        assertThat(orderDetailResponse.getTotalPrice()).isEqualTo(3900L);
        assertThat(orderDetailResponse.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(orderDetailResponse.getDeliveryStatus()).isEqualTo(DeliveryStatus.PREPARING);
        assertThat(orderDetailResponse.getDeliveryExpectedDate()).isEqualTo(LocalDate.of
                (2026, 7, 23));
        assertThat(orderDetailResponse.getOrderedAt()).isEqualTo(LocalDateTime.of
                (2026, 6, 20,0,0,0));

        List<OrderItemResponse> items = response.get().getItems();
        assertThat(items.get(0).getProductName()).isEqualTo("배");
        assertThat(items.get(0).getQuantity()).isEqualTo(3);
        assertThat(items.get(0).getUnitPrice()).isEqualTo(300L);
        assertThat(items.get(0).getSubtotal()).isEqualTo(900L);
    }

    @Test
    @DisplayName("비었을 때 값 검증")
    void findAll() {
        //given
        int size = 10;

        //when
        Pageable pageable = PageRequest.of(0, size);
        Page<OrderSummaryResponse> orderSummaryResponsePage = orderRepository.getOrders("", pageable);
        List<OrderSummaryResponse> orders = orderSummaryResponsePage.getContent();

        //then
        //페이지가 있음
        assertThat(orderSummaryResponsePage).isNotNull();
        assertThat(orderSummaryResponsePage.getTotalElements()).isEqualTo(4); //총 데이터 갯수?
//        assertThat(orderSummaryResponsePage.isEmpty()); page 2 일때
        // 값 검증
        assertThat(orders.get(0).getTotalPrice()).isEqualTo(2300L);


    }

    @Test
    @DisplayName("이메일을 주었을 때 값 검증")
    void findByEmail() {
        //given
        int page = 1;
        int size = 10;

        //when
        Pageable pageable = PageRequest.of(0, size);
        Page<OrderSummaryResponse> orderSummaryResponsePage = orderRepository.getOrders("test@naver.com", pageable);
        List<OrderSummaryResponse> orders = orderSummaryResponsePage.getContent();

        //then
        //페이지가 있음
        assertThat(orderSummaryResponsePage).isNotNull();
        assertThat(orderSummaryResponsePage.getTotalElements()).isEqualTo(2); //총 데이터 갯수?

        // 값 검증
        assertThat(orders.get(0).getTotalPrice()).isEqualTo(5700L);

    }

}