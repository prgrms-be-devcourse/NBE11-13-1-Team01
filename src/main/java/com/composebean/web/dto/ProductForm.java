package com.composebean.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

    private String name;
    private Long price;
    private String description;
    private String imageUrl;
    private Integer stockQuantity;
}
