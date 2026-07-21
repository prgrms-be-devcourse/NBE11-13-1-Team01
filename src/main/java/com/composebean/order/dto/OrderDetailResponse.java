package com.composebean.order.dto;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.domain.OrderItem;
import com.composebean.order.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "주문 상세 응답")
@Getter
@Builder
public class OrderDetailResponse {

    @Schema(description = "주문 ID", example = "15")
    private Long id;

    @Schema(description = "주문자 이메일", example = "customer@example.com")
    private String email;

    @Schema(
            description = "배송 주소",
            example = "서울특별시 강남구 테헤란로 123"
    )
    private String address;

    @Schema(description = "우편번호", example = "06234")
    private String postalCode;

    @Schema(description = "총 결제 금액", example = "16000")
    private Long totalPrice;

    @Schema(description = "결제 상태", example = "PAID")
    private PaymentStatus paymentStatus;

    @Schema(description = "배송 상태", example = "PREPARING")
    private DeliveryStatus deliveryStatus;

    @Schema(description = "배송 예정일", example = "2026-07-24")
    private LocalDate deliveryExpectedDate;

    @Schema(description = "주문 시각", example = "2026-07-21T13:30:00")
    private LocalDateTime orderedAt;

    @Schema(description = "주문 상품 목록")
    private List<OrderItemResponse> items;

    public static OrderDetailResponse from(Order order) {
        List<OrderItemResponse> items = new ArrayList<>();

        for (OrderItem orderItem : order.getOrderItems()) {
            items.add(OrderItemResponse.from(orderItem));
        }

        return OrderDetailResponse.builder()
                .id(order.getId())
                .email(order.getEmail())
                .address(order.getAddress())
                .postalCode(order.getPostalCode())
                .totalPrice(order.getTotalPrice())
                .paymentStatus(order.getPaymentStatus())
                .deliveryStatus(order.getDeliveryStatus())
                .deliveryExpectedDate(
                        order.getDeliveryExpectedDate().toLocalDate()
                )
                .orderedAt(order.getOrderedAt())
                .items(items)
                .build();
    }
}