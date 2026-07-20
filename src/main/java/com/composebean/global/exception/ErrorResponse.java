package com.composebean.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Schema(description = "공통 오류 응답")
@Getter
@Builder
public class ErrorResponse {

    @Schema(
            description = "오류 코드",
            example = "PRODUCT_NOT_FOUND"
    )
    private final String code;

    @Schema(
            description = "오류 메시지",
            example = "상품을 찾을 수 없습니다."
    )
    private final String message;

    @Schema(
            description = "필드별 요청값 검증 오류",
            example = "{\"name\":\"상품명은 필수입니다.\"}"
    )
    private final Map<String, String> errors;

    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(Map.of())
                .build();
    }

    public static ErrorResponse of(
            ErrorCode errorCode,
            String message
    ) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .errors(Map.of())
                .build();
    }

    public static ErrorResponse of(
            ErrorCode errorCode,
            Map<String, String> errors
    ) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();
    }
}