package com.composebean.product.dto;

import com.composebean.product.domain.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "상품 등록 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @Schema(description = "상품명", example = "콜롬비아 원두")
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
    private String name;

    @Schema(description = "상품 가격", example = "18000")
    @NotNull(message = "상품 가격은 필수입니다.")
    @Min(value = 0, message = "상품 가격은 0원 이상이어야 합니다.")
    private Long price;

    @Schema(
            description = "상품 설명",
            example = "산미와 단맛이 균형 잡힌 콜롬비아 원두"
    )
    @Size(max = 255, message = "상품 설명은 255자 이하여야 합니다.")
    private String description;

    @Schema(
            description = "상품 이미지 URL",
            example = "https://example.com/images/colombia-beans.jpg"
    )
    @Size(max = 500, message = "상품 이미지 URL은 500자 이하여야 합니다.")
    private String imageUrl;

    @Schema(description = "재고 수량", example = "100")
    @NotNull(message = "재고 수량은 필수입니다.")
    @Min(value = 0, message = "재고 수량은 0개 이상이어야 합니다.")
    private Integer stockQuantity;

    public Product toEntity() {
        return Product.builder()
                .name(name)
                .price(price)
                .description(description)
                .imageUrl(imageUrl)
                .stockQuantity(stockQuantity)
                .build();
    }
}