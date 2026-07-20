package com.composebean.order.dto;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderCreateResponse {

    private Long orderId;
    private String email;
    private Long totalPrice;
    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;
    private LocalDate deliveryDate;
    private LocalDateTime orderedAt;

    public static OrderCreateResponse from(Order order) {
        return new OrderCreateResponse(
                order.getId(),
                order.getEmail(),
                order.getTotalPrice(),
                order.getPaymentStatus(),
                order.getDeliveryStatus(),
                order.getDeliveryExpectedDate().toLocalDate(),
                order.getOrderedAt()
        );
    }
}
