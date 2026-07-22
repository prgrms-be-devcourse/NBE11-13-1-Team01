package com.composebean.product.controller;

import com.composebean.global.exception.GlobalExceptionHandler;
import com.composebean.product.dto.ProductListResponse;
import com.composebean.product.dto.ProductResponse;
import com.composebean.product.exception.ProductNotFoundException;
import com.composebean.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("상품 목록을 조회한다")
    void getProducts() throws Exception {
        ProductResponse product = createProductResponse();

        when(productService.getProducts(null))
                .thenReturn(
                        new ProductListResponse(
                                List.of(product)
                        )
                );

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(
                        jsonPath("$.products.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.products[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.products[0].name")
                                .value("콜롬비아 원두")
                )
                .andExpect(
                        jsonPath("$.products[0].price")
                                .value(18000)
                )
                .andExpect(
                        jsonPath("$.products[0].stockQuantity")
                                .value(100)
                );

        verify(productService).getProducts(null);
    }

    @Test
    @DisplayName("상품명으로 상품 목록을 검색한다")
    void searchProductsByName() throws Exception {
        ProductResponse product = createProductResponse();

        when(productService.getProducts("원두"))
                .thenReturn(
                        new ProductListResponse(
                                List.of(product)
                        )
                );

        mockMvc.perform(
                        get("/api/products")
                                .param("name", "원두")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.products.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.products[0].name")
                                .value("콜롬비아 원두")
                );

        verify(productService).getProducts("원두");
    }

    @Test
    @DisplayName("상품 ID로 상품을 조회한다")
    void getProduct() throws Exception {
        when(productService.getProduct(1L))
                .thenReturn(createProductResponse());

        mockMvc.perform(
                        get(
                                "/api/products/{productId}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.name")
                                .value("콜롬비아 원두")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(18000)
                )
                .andExpect(
                        jsonPath("$.description")
                                .value(
                                        "산미와 단맛이 균형 잡힌 원두"
                                )
                )
                .andExpect(
                        jsonPath("$.stockQuantity")
                                .value(100)
                );

        verify(productService).getProduct(1L);
    }

    @Test
    @DisplayName("상품을 등록한다")
    void createProduct() throws Exception {
        MockMultipartFile imageFile =
                new MockMultipartFile(
                        "imageFile",
                        "colombia-beans.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "image-content".getBytes()
                );

        when(productService.createProduct(any()))
                .thenReturn(createProductResponse());

        mockMvc.perform(
                        multipart("/api/products")
                                .file(imageFile)
                                .param("name", "콜롬비아 원두")
                                .param("price", "18000")
                                .param(
                                        "description",
                                        "산미와 단맛이 균형 잡힌 원두"
                                )
                                .param("stockQuantity", "100")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.name")
                                .value("콜롬비아 원두")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(18000)
                )
                .andExpect(
                        jsonPath("$.stockQuantity")
                                .value(100)
                );

        verify(productService).createProduct(any());
    }

    @Test
    @DisplayName("상품 등록 요청값이 올바르지 않으면 400을 반환한다")
    void createProductValidationFail() throws Exception {
        mockMvc.perform(
                        multipart("/api/products")
                                .param("name", "")
                                .param("price", "-1")
                                .param(
                                        "description",
                                        "테스트 상품"
                                )
                                .param("stockQuantity", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.code")
                                .value("INVALID_REQUEST")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("요청값이 올바르지 않습니다.")
                )
                .andExpect(
                        jsonPath("$.errors.name")
                                .value("상품명은 필수입니다.")
                )
                .andExpect(
                        jsonPath("$.errors.price")
                                .value(
                                        "상품 가격은 0원 이상이어야 합니다."
                                )
                )
                .andExpect(
                        jsonPath("$.errors.stockQuantity")
                                .value(
                                        "재고 수량은 0개 이상이어야 합니다."
                                )
                );
    }

    @Test
    @DisplayName("상품 정보를 수정한다")
    void updateProduct() throws Exception {
        MockMultipartFile imageFile =
                new MockMultipartFile(
                        "imageFile",
                        "ethiopia-beans.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "new-image-content".getBytes()
                );

        ProductResponse response =
                ProductResponse.builder()
                        .id(1L)
                        .name("에티오피아 원두")
                        .price(20000L)
                        .description(
                                "꽃향과 산미가 특징인 원두"
                        )
                        .imageUrl(
                                "/uploads/products/"
                                        + "ethiopia-beans.jpg"
                        )
                        .stockQuantity(100)
                        .createdAt(
                                LocalDateTime.of(
                                        2026,
                                        7,
                                        20,
                                        15,
                                        44,
                                        42
                                )
                        )
                        .updatedAt(
                                LocalDateTime.of(
                                        2026,
                                        7,
                                        20,
                                        16,
                                        2,
                                        34
                                )
                        )
                        .build();

        when(
                productService.updateProduct(
                        eq(1L),
                        any()
                )
        ).thenReturn(response);

        mockMvc.perform(
                        multipart(
                                "/api/products/{productId}",
                                1L
                        )
                                .file(imageFile)
                                .param("name", "에티오피아 원두")
                                .param("price", "20000")
                                .param(
                                        "description",
                                        "꽃향과 산미가 특징인 원두"
                                )
                                .param("deleteImage", "false")
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.name")
                                .value("에티오피아 원두")
                )
                .andExpect(
                        jsonPath("$.price")
                                .value(20000)
                )
                .andExpect(
                        jsonPath("$.stockQuantity")
                                .value(100)
                );

        verify(productService)
                .updateProduct(eq(1L), any());
    }

    @Test
    @DisplayName("상품 재고를 수정한다")
    void updateStock() throws Exception {
        ProductResponse response =
                ProductResponse.builder()
                        .id(1L)
                        .name("콜롬비아 원두")
                        .price(18000L)
                        .description(
                                "산미와 단맛이 균형 잡힌 원두"
                        )
                        .imageUrl(
                                "https://example.com/images/"
                                        + "colombia-beans.jpg"
                        )
                        .stockQuantity(75)
                        .createdAt(
                                LocalDateTime.of(
                                        2026,
                                        7,
                                        20,
                                        15,
                                        44,
                                        42
                                )
                        )
                        .updatedAt(
                                LocalDateTime.of(
                                        2026,
                                        7,
                                        20,
                                        16,
                                        10,
                                        0
                                )
                        )
                        .build();

        when(
                productService.updateStock(
                        eq(1L),
                        any()
                )
        ).thenReturn(response);

        String request = """
                {
                  "stockQuantity": 75
                }
                """;

        mockMvc.perform(
                        patch(
                                "/api/products/{productId}/stock",
                                1L
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.stockQuantity")
                                .value(75)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("콜롬비아 원두")
                );

        verify(productService)
                .updateStock(eq(1L), any());
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void deleteProduct() throws Exception {
        mockMvc.perform(
                        delete(
                                "/api/products/{productId}",
                                1L
                        )
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(productService).deleteProduct(1L);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 404를 반환한다")
    void getProductNotFound() throws Exception {
        when(productService.getProduct(999L))
                .thenThrow(
                        new ProductNotFoundException()
                );

        mockMvc.perform(
                        get(
                                "/api/products/{productId}",
                                999L
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.code")
                                .value(
                                        "PRODUCT_NOT_FOUND"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "상품을 찾을 수 없습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.errors").isEmpty()
                );
    }

    @Test
    @DisplayName("상품 가격 타입이 올바르지 않으면 400을 반환한다")
    void invalidPriceType() throws Exception {
        mockMvc.perform(
                        multipart("/api/products")
                                .param(
                                        "name",
                                        "콜롬비아 원두"
                                )
                                .param(
                                        "price",
                                        "비쌈"
                                )
                                .param(
                                        "description",
                                        "산미와 단맛이 균형 잡힌 원두"
                                )
                                .param(
                                        "stockQuantity",
                                        "100"
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.code")
                                .value("INVALID_REQUEST")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "요청값이 올바르지 않습니다."
                                )
                );
    }

    private ProductResponse createProductResponse() {
        return ProductResponse.builder()
                .id(1L)
                .name("콜롬비아 원두")
                .price(18000L)
                .description(
                        "산미와 단맛이 균형 잡힌 원두"
                )
                .imageUrl(
                        "https://example.com/images/"
                                + "colombia-beans.jpg"
                )
                .stockQuantity(100)
                .createdAt(
                        LocalDateTime.of(
                                2026,
                                7,
                                20,
                                15,
                                44,
                                42
                        )
                )
                .updatedAt(
                        LocalDateTime.of(
                                2026,
                                7,
                                20,
                                15,
                                44,
                                42
                        )
                )
                .build();
    }
}