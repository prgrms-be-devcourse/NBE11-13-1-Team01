package com.composebean.order.service;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
import com.composebean.order.domain.DeliveryAreaDuration;
import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import com.composebean.order.dto.OrderCreateRequest;
import com.composebean.order.dto.OrderCreateResponse;
import com.composebean.order.dto.OrderItemRequest;
import com.composebean.order.event.OrderCreatedEvent;
import com.composebean.order.repository.OrderRepository;
import com.composebean.product.domain.Product;
import com.composebean.product.exception.ProductNotFoundException;
import com.composebean.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request) {
        validateAddress(request);

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
            Product product = productRepository
                    .findByIdForUpdate(itemRequest.getProductId())
                    .orElseThrow(ProductNotFoundException::new);

            validateQuantity(
                    itemRequest.getQuantity(),
                    product.getStockQuantity()
            );

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

        List<OrderCreatedEvent.OrderItemInfo> eventItems =
                savedOrder.getOrderItems().stream()
                        .map(orderItem ->
                                new OrderCreatedEvent.OrderItemInfo(
                                        orderItem.getProduct().getName(),
                                        orderItem.getQuantity(),
                                        orderItem.getUnitPrice(),
                                        orderItem.getSubtotal()
                                )
                        )
                        .toList();

        eventPublisher.publishEvent(
                new OrderCreatedEvent(
                        savedOrder.getId(),
                        savedOrder.getEmail(),
                        savedOrder.getAddress(),
                        savedOrder.getPostalCode(),
                        savedOrder.getTotalPrice(),
                        savedOrder.getOrderedAt(),
                        eventItems
                )
        );

        return OrderCreateResponse.from(savedOrder);
    }

    private void validateAddress(OrderCreateRequest request) {
        if (request == null
                || request.getAddress() == null
                || request.getAddress().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_REQUEST);
        }
    }

    private void validateOrderRequest(
            OrderCreateRequest request,
            LocalDateTime deliveryExpectedDate
    ) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_REQUEST);
        }

        if (deliveryExpectedDate == null) {
            throw new BusinessException(ErrorCode.INVALID_DELIVERY_DATE);
        }
    }

    private void validateQuantity(
            Integer quantity,
            Integer stockQuantity
    ) {
        if (quantity == null || quantity < 1) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_QUANTITY);
        }

        if (quantity > stockQuantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }
}
