package com.composebean.order.dto;

import com.composebean.order.domain.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "주문 상품 응답")
@Getter
@Builder
public class OrderItemResponse {

    @Schema(description = "주문 상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품명", example = "Colombia Nariño")
    private String productName;

    @Schema(description = "주문 수량", example = "2")
    private Integer quantity;

    @Schema(description = "주문 당시 단가", example = "5000")
    private Long unitPrice;

    @Schema(description = "상품별 주문 금액", example = "10000")
    private Long subtotal;

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }
}