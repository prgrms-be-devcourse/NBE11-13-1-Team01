package com.composebean.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "주문 생성 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @Schema(description = "주문자 이메일", example = "customer@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일을 입력해 주세요.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "도메인을 포함한 올바른 이메일을 입력해 주세요."
    )
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @Schema(
            description = "배송 주소",
            example = "서울특별시 강남구 테헤란로 123"
    )
    @NotBlank(message = "주소는 필수입니다.")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @Schema(description = "우편번호", example = "06234")
    @NotBlank(message = "우편번호는 필수입니다.")
    @Size(max = 20, message = "우편번호는 20자 이하여야 합니다.")
    private String postalCode;

    @Schema(description = "주문 상품 목록")
    @NotEmpty(message = "주문 상품은 한 개 이상이어야 합니다.")
    @Valid
    private List<OrderItemRequest> items;
}
