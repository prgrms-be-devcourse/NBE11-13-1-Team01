package com.composebean.order.service;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.DeliveryAreaDuration;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.OrderCreateRequest;
import com.composebean.order.dto.OrderCreateResponse;
import com.composebean.order.dto.OrderItemRequest;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.exception.ProductNotFoundException;
import com.composebean.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCreateService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request) {
        if (request == null || request.getAddress() == null) {
            throw new IllegalArgumentException("주소는 필수입니다.");
        }

        int deliveryDays = DeliveryAreaDuration.getDeliveryDaysByAddress(
                request.getAddress()
        );
        LocalDateTime deliveryExpectedDate = LocalDateTime.now()
                .plusDays(deliveryDays);

        return createOrder(request, deliveryExpectedDate);
    }

    @Transactional
    public OrderCreateResponse createOrder(
            OrderCreateRequest request,
            LocalDateTime deliveryExpectedDate
    ) {
        validateOrderRequest(request, deliveryExpectedDate);

        List<OrderItem> orderItems = new ArrayList<>();
        long totalPrice = 0L;

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(ProductNotFoundException::new);

            validateQuantity(itemRequest.getQuantity(), product.getStockQuantity());

            long unitPrice = product.getPrice();
            long subtotal = unitPrice * itemRequest.getQuantity();
            totalPrice += subtotal;

            product.decreaseStock(itemRequest.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .email(request.getEmail())
                .address(request.getAddress())
                .postalCode(request.getPostalCode())
                .totalPrice(totalPrice)
                .paymentStatus(PaymentStatus.PAID)
                .deliveryStatus(DeliveryStatus.PREPARING)
                .deliveryExpectedDate(deliveryExpectedDate)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        return OrderCreateResponse.from(savedOrder);
    }

    private void validateOrderRequest(
            OrderCreateRequest request,
            LocalDateTime deliveryExpectedDate
    ) {
        if (request == null) {
            throw new IllegalArgumentException("주문 정보는 필수입니다.");
        }
        if (request.getEmail() == null
                || request.getEmail().isBlank()
                || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("올바른 이메일을 입력해 주세요.");
        }
        if (request.getAddress() == null || request.getAddress().isBlank()) {
            throw new IllegalArgumentException("주소는 필수입니다.");
        }
        if (request.getPostalCode() == null || request.getPostalCode().isBlank()) {
            throw new IllegalArgumentException("우편번호는 필수입니다.");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 상품은 한 개 이상이어야 합니다.");
        }
        if (deliveryExpectedDate == null) {
            throw new IllegalArgumentException("배송 예정일은 필수입니다.");
        }
    }

    private void validateQuantity(Integer quantity, Integer stockQuantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        }
        if (quantity > stockQuantity) {
            throw new IllegalArgumentException("상품 재고가 부족합니다.");
        }
    }
}
