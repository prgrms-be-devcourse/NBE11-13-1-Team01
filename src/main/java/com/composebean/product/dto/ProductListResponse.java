package com.composebean.product.dto;

import com.composebean.product.domain.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Schema(description = "상품 목록 응답")
@Getter
@RequiredArgsConstructor
public class ProductListResponse {

    @Schema(description = "상품 목록")
    private final List<ProductResponse> products;

    public static ProductListResponse from(List<Product> products) {
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::from)
                .toList();

        return new ProductListResponse(responses);
    }
}