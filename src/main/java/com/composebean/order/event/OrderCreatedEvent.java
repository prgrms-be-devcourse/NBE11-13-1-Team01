package com.composebean.order.event;

import java.time.LocalDateTime;
import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        String email,
        String address,
        String postalCode,
        Long totalPrice,
        LocalDateTime orderedAt,
        List<OrderItemInfo> items
) {

    public record OrderItemInfo(
            String productName,
            Integer quantity,
            Long unitPrice,
            Long subtotal
    ) {
    }
}