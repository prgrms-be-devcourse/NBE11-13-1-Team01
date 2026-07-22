package com.composebean.web.dto;

import com.composebean.order.dto.OrderCreateRequest;
import com.composebean.order.dto.OrderItemRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderForm {

    private String email;
    private String deliveryRegion;
    private String address;
    private String postalCode;
    private List<OrderItemForm> items = new ArrayList<>();

    public OrderCreateRequest toRequest() {
        List<OrderItemRequest> itemRequests = new ArrayList<>();

        for (OrderItemForm item : items) {
            itemRequests.add(OrderItemRequest.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build());
        }

        return OrderCreateRequest.builder()
                .email(email)
                .address(deliveryRegion + " " + address)
                .postalCode(postalCode)
                .items(itemRequests)
                .build();
    }
}
