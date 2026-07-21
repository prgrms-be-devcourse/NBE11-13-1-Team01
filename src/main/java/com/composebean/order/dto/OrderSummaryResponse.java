package com.composebean.order.dto;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.PaymentStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "간단 주문 명세 응답")
@Getter
@Builder
public class OrderSummaryResponse {

    @Schema(description = "주문 ID", example = "15")
    private Long id;

    @Schema(description = "주문자 이메일", example = "customer@example.com")
    private String email;

    @Schema(description = "총 결제 금액", example = "16000")
    private Long totalPrice;

    @Schema(description = "결제 상태", example = "PAID")
    private PaymentStatus paymentStatus;

    @Schema(description = "배송 상태", example = "PREPARING")
    private DeliveryStatus deliveryStatus;

    @Schema(description = "배송 예정일", example = "2026-07-24")
    private LocalDateTime deliveryExpectedDate;

    @Schema(description = "주문 시각", example = "2026-07-21T13:30:00")
    private LocalDateTime orderedAt;

    @QueryProjection
    public OrderSummaryResponse(Long id, String email, Long totalPrice, PaymentStatus paymentStatus, DeliveryStatus deliveryStatus, LocalDateTime deliveryExpectedDate, LocalDateTime orderedAt) {
        this.id = id;
        this.email = email;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.deliveryStatus = deliveryStatus;
        this.deliveryExpectedDate = deliveryExpectedDate;
        this.orderedAt = orderedAt;
    }
}
