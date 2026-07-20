package com.composebean.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "상품 재고 수정 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockUpdateRequest {

    @Schema(description = "변경할 최종 재고 수량", example = "100")
    @NotNull(message = "재고 수량은 필수입니다.")
    @Min(value = 0, message = "재고 수량은 0개 이상이어야 합니다.")
    private Integer stockQuantity;
}