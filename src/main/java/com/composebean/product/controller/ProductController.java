package com.composebean.product.controller;

import com.composebean.global.exception.ErrorResponse;
import com.composebean.product.dto.ProductCreateRequest;
import com.composebean.product.dto.ProductListResponse;
import com.composebean.product.dto.ProductResponse;
import com.composebean.product.dto.ProductStockUpdateRequest;
import com.composebean.product.dto.ProductUpdateRequest;
import com.composebean.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "상품 API",
        description = "상품 등록, 조회, 수정, 삭제 및 재고 관리 API"
)
@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "상품 목록 조회",
            description = "전체 상품 목록을 조회한다. name을 전달하면 상품명에 해당 문자열이 포함된 상품을 검색한다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 목록 조회 성공"
    )
    @GetMapping
    public ResponseEntity<ProductListResponse> getProducts(
            @Parameter(
                    description = "검색할 상품명",
                    example = "원두"
            )
            @RequestParam(required = false)
            String name
    ) {
        return ResponseEntity.ok(
                productService.getProducts(name)
        );
    }

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 ID에 해당하는 상품 정보를 조회한다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 상세 조회 성공"
    )
    @ApiResponse(
            responseCode = "400",
            description = "올바르지 않은 상품 ID",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "상품을 찾을 수 없음",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(
                    description = "상품 ID",
                    example = "1"
            )
            @Positive(message = "상품 ID는 1 이상이어야 합니다.")
            @PathVariable
            Long productId
    ) {
        return ResponseEntity.ok(
                productService.getProduct(productId)
        );
    }

    @Operation(
            summary = "상품 등록",
            description = "상품 정보와 이미지 파일을 multipart/form-data 형식으로 전달하여 상품을 등록한다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "상품 등록 성공"
    )
    @ApiResponse(
            responseCode = "400",
            description = "요청값 또는 이미지 파일 검증 실패",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductResponse> createProduct(
            @Valid
            @ModelAttribute
            ProductCreateRequest request
    ) {
        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "상품 정보 수정",
            description = "상품 ID에 해당하는 상품의 이름, 가격, 설명과 이미지 파일을 수정한다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 정보 수정 성공"
    )
    @ApiResponse(
            responseCode = "400",
            description = "요청값 검증 실패",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "상품을 찾을 수 없음",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @PutMapping(
            value = "/{productId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "상품 ID", example = "1")
            @Positive(message = "상품 ID는 1 이상이어야 합니다.")
            @PathVariable Long productId,

            @Valid @ModelAttribute ProductUpdateRequest request
    ) {
        return ResponseEntity.ok(
                productService.updateProduct(productId, request)
        );
    }

    @Operation(
            summary = "상품 재고 수정",
            description = "상품 ID에 해당하는 상품의 재고 수량을 입력한 값으로 변경한다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "상품 재고 수정 성공"
    )
    @ApiResponse(
            responseCode = "400",
            description = "요청값 또는 이미지 파일 검증 실패",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "상품을 찾을 수 없음",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @PatchMapping("/{productId}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @Parameter(
                    description = "상품 ID",
                    example = "1"
            )
            @Positive(message = "상품 ID는 1 이상이어야 합니다.")
            @PathVariable
            Long productId,

            @Valid
            @RequestBody
            ProductStockUpdateRequest request
    ) {
        return ResponseEntity.ok(
                productService.updateStock(
                        productId,
                        request
                )
        );
    }

    @Operation(
            summary = "상품 삭제",
            description = "상품 ID에 해당하는 상품을 삭제한다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "상품 삭제 성공"
    )
    @ApiResponse(
            responseCode = "400",
            description = "올바르지 않은 상품 ID",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "상품을 찾을 수 없음",
            content = @Content(
                    schema = @Schema(
                            implementation = ErrorResponse.class
                    )
            )
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(
                    description = "상품 ID",
                    example = "1"
            )
            @Positive(message = "상품 ID는 1 이상이어야 합니다.")
            @PathVariable
            Long productId
    ) {
        productService.deleteProduct(productId);

        return ResponseEntity
                .noContent()
                .build();
    }
}