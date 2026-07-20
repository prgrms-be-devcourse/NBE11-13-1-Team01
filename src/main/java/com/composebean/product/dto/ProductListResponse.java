package com.composebean.product.dto;

import com.composebean.product.domain.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProductListResponse {

    private final List<ProductResponse> products;

    public static ProductListResponse from(List<Product> products) {
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::from)
                .toList();

        return new ProductListResponse(responses);
    }
}