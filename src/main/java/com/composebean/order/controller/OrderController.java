package com.composebean.order.controller;

import com.composebean.global.exception.ErrorResponse;
import com.composebean.order.dto.OrderCreateRequest;
import com.composebean.order.dto.OrderCreateResponse;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.service.OrderCreateService;
import com.composebean.order.service.OrderDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "주문 API", description = "주문 생성, 결제 및 영수증 조회 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderCreateService orderCreateService;
    private final OrderDetailService orderDetailService;

    @Operation(
            summary = "주문 생성 및 결제",
            description = "주문 상품과 수량을 검증하고 재고를 차감한 뒤 결제 완료 상태로 주문을 생성한다."
    )
    @ApiResponse(responseCode = "201", description = "주문 생성 성공")
    @ApiResponse(
            responseCode = "400",
            description = "요청값 또는 재고 검증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "상품을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request
    ) {
        OrderCreateResponse response = orderCreateService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "주문 상세 및 영수증 조회",
            description = "주문 ID로 주문자, 결제, 배송 및 주문 상품 정보를 조회한다."
    )
    @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공")
    @ApiResponse(
            responseCode = "404",
            description = "주문을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderDetailService.getOrder(orderId));
    }

}
