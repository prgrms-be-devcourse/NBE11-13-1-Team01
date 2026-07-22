package com.composebean.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "INVALID_REQUEST",
            "요청값이 올바르지 않습니다."
    ),

    PRODUCT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PRODUCT_NOT_FOUND",
            "상품을 찾을 수 없습니다."
    ),

    ORDER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "ORDER_NOT_FOUND",
            "주문을 찾을 수 없습니다."
    ),

    INVALID_ORDER_REQUEST(
            HttpStatus.BAD_REQUEST,
            "INVALID_ORDER_REQUEST",
            "주문 요청값이 올바르지 않습니다."
    ),

    INVALID_DELIVERY_DATE(
            HttpStatus.BAD_REQUEST,
            "INVALID_DELIVERY_DATE",
            "배송 예정일은 필수입니다."
    ),

    INVALID_ORDER_QUANTITY(
            HttpStatus.BAD_REQUEST,
            "INVALID_ORDER_QUANTITY",
            "주문 수량은 1개 이상이어야 합니다."
    ),

    INSUFFICIENT_STOCK(
            HttpStatus.BAD_REQUEST,
            "INSUFFICIENT_STOCK",
            "상품 재고가 부족합니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다."
    ),

    INVALID_IMAGE_FILE(
            HttpStatus.BAD_REQUEST,
            "INVALID_IMAGE_FILE",
            "올바른 이미지 파일을 입력해 주세요."
    ),
    IMAGE_STORAGE_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "IMAGE_STORAGE_FAILED",
            "이미지 파일 저장에 실패했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}