package com.composebean.order.dto;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "주문 생성 응답")
@Getter
@Builder
public class OrderCreateResponse {

    @Schema(description = "주문 ID", example = "1")
    private Long orderId;

    @Schema(description = "주문자 이메일", example = "customer@example.com")
    private String email;

    @Schema(description = "총 결제 금액", example = "16000")
    private Long totalPrice;

    @Schema(description = "결제 상태", example = "PAID")
    private PaymentStatus paymentStatus;

    @Schema(description = "배송 상태", example = "PREPARING")
    private DeliveryStatus deliveryStatus;

    @Schema(description = "배송 예정일", example = "2026-07-24")
    private LocalDate deliveryDate;

    @Schema(description = "주문 시각", example = "2026-07-21T13:30:00")
    private LocalDateTime orderedAt;

    public static OrderCreateResponse from(Order order) {
        return OrderCreateResponse.builder()
                .orderId(order.getId())
                .email(order.getEmail())
                .totalPrice(order.getTotalPrice())
                .paymentStatus(order.getPaymentStatus())
                .deliveryStatus(order.getDeliveryStatus())
                .deliveryDate(order.getDeliveryExpectedDate().toLocalDate())
                .orderedAt(order.getOrderedAt())
                .build();
    }
}
