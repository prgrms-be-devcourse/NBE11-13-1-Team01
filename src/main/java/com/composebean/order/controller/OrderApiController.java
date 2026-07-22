package com.composebean.order.controller;

import com.composebean.global.exception.ErrorResponse;
import com.composebean.order.dto.DeliveryStatusUpdateRequest;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.dto.OrderSummaryResponse;
import com.composebean.order.service.OrderInquiryService;
import com.composebean.order.service.OrderUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Tag(name = "주문 API", description = "주문 검색 및 주문 세부 배송 상태 변경")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderInquiryService orderInquiryService;
    private final OrderUpdateService orderUpdateService;

    @Operation(
            summary = "주문(이메일) 검색",
            description = "단순 이메일로 동적 검색한다. 이메일이 완전히 같아야 조회가 가능하다."
    )
    @GetMapping
    public Page<OrderSummaryResponse> getOrders(
            @RequestParam(required = false) String email,

            @Parameter(
                    description = "조회할 페이지 번호 (1부터 시작)",
                    example = "1"
            )
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
            int page,

            @Parameter(
                    description = "한 페이지에 담을 주문 수",
                    example = "8"
            )
            @RequestParam(defaultValue = "8")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
            @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
            int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);

        return orderInquiryService.getOrders(email, pageable);
    }

    @Operation(
            summary = "주문_배송 상태 변경",
            description = "orderId에서 받은 배송 상태 요청으로 상태를 업데이트 한다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "배송 상태 변경 완료"
    )
    @ApiResponse(
            responseCode = "404",
            description = "주문을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "배송 상태 유효성 검증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{orderId}/delivery-status")
    public ResponseEntity<OrderDetailResponse> updateDeliveryStatus(
            @Valid @RequestBody DeliveryStatusUpdateRequest dto,
            @PathVariable
            @Positive(message = "주문 ID는 양수여야 합니다.")
            Long orderId
    ) {
        return ResponseEntity.ok(orderUpdateService.updateDeliveryStatus(dto, orderId));
    }
}
