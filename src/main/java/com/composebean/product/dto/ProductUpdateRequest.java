package com.composebean.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "상품 정보 수정 요청")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

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
            description = "변경할 상품 이미지 파일",
            type = "string",
            format = "binary"
    )
    private MultipartFile imageFile;

    @Schema(description = "기존 상품 이미지 삭제 여부", example = "false")
    private boolean deleteImage;
}