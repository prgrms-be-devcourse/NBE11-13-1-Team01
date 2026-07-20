package com.composebean.product.dto;

import com.composebean.product.domain.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "상품 응답")
@Getter
@Builder
public class ProductResponse {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품명", example = "콜롬비아 원두")
    private String name;

    @Schema(description = "상품 가격", example = "18000")
    private Long price;

    @Schema(
            description = "상품 설명",
            example = "산미와 단맛이 균형 잡힌 콜롬비아 원두"
    )
    private String description;

    @Schema(
            description = "상품 이미지 URL",
            example = "https://example.com/images/colombia-beans.jpg"
    )
    private String imageUrl;

    @Schema(description = "재고 수량", example = "100")
    private Integer stockQuantity;

    @Schema(
            description = "상품 등록 일시",
            example = "2026-07-20T10:00:00"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "상품 수정 일시",
            example = "2026-07-20T10:30:00"
    )
    private LocalDateTime updatedAt;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .stockQuantity(product.getStockQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}