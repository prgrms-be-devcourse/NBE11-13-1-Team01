package com.composebean.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemForm {

    private Long productId;
    private Integer quantity;
}
