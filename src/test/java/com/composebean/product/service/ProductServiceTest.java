package com.composebean.product.service;

import com.composebean.product.domain.Product;
import com.composebean.product.dto.ProductCreateRequest;
import com.composebean.product.dto.ProductListResponse;
import com.composebean.product.dto.ProductResponse;
import com.composebean.product.dto.ProductStockUpdateRequest;
import com.composebean.product.dto.ProductUpdateRequest;
import com.composebean.product.exception.ProductNotFoundException;
import com.composebean.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .name("콜롬비아 원두")
                .price(18000L)
                .description("산미와 단맛이 균형 잡힌 원두")
                .imageUrl("https://example.com/images/colombia-beans.jpg")
                .stockQuantity(100)
                .build();
    }

    @Test
    @DisplayName("상품을 등록한다")
    void createProduct() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("콜롬비아 원두")
                .price(18000L)
                .description("산미와 단맛이 균형 잡힌 원두")
                .imageUrl("https://example.com/images/colombia-beans.jpg")
                .stockQuantity(100)
                .build();

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.createProduct(request);

        assertThat(response.getName()).isEqualTo("콜롬비아 원두");
        assertThat(response.getPrice()).isEqualTo(18000L);
        assertThat(response.getDescription())
                .isEqualTo("산미와 단맛이 균형 잡힌 원두");
        assertThat(response.getStockQuantity()).isEqualTo(100);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("검색어가 없으면 전체 상품을 조회한다")
    void getProductsWithoutName() {
        when(productRepository.findAll())
                .thenReturn(List.of(product));

        ProductListResponse response = productService.getProducts(null);

        assertThat(response.getProducts()).hasSize(1);
        assertThat(response.getProducts().get(0).getName())
                .isEqualTo("콜롬비아 원두");

        verify(productRepository).findAll();
        verify(productRepository, never())
                .findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("검색어가 공백이면 전체 상품을 조회한다")
    void getProductsWithBlankName() {
        when(productRepository.findAll())
                .thenReturn(List.of(product));

        ProductListResponse response = productService.getProducts("   ");

        assertThat(response.getProducts()).hasSize(1);

        verify(productRepository).findAll();
        verify(productRepository, never())
                .findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("상품명에 검색어가 포함된 상품을 조회한다")
    void getProductsWithName() {
        when(productRepository.findByNameContainingIgnoreCase("원두"))
                .thenReturn(List.of(product));

        ProductListResponse response = productService.getProducts("원두");

        assertThat(response.getProducts()).hasSize(1);
        assertThat(response.getProducts().get(0).getName())
                .contains("원두");

        verify(productRepository)
                .findByNameContainingIgnoreCase("원두");
        verify(productRepository, never()).findAll();
    }

    @Test
    @DisplayName("상품 ID로 상품을 조회한다")
    void getProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.getProduct(1L);

        assertThat(response.getName()).isEqualTo("콜롬비아 원두");
        assertThat(response.getPrice()).isEqualTo(18000L);

        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 조회하면 예외가 발생한다")
    void getProductNotFound() {
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(999L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findById(999L);
    }

    @Test
    @DisplayName("상품 정보를 수정한다")
    void updateProduct() {
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("에티오피아 원두")
                .price(20000L)
                .description("꽃향과 산미가 특징인 원두")
                .imageUrl("https://example.com/images/ethiopia-beans.jpg")
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.updateProduct(1L, request);

        assertThat(response.getName()).isEqualTo("에티오피아 원두");
        assertThat(response.getPrice()).isEqualTo(20000L);
        assertThat(response.getDescription())
                .isEqualTo("꽃향과 산미가 특징인 원두");
        assertThat(response.getImageUrl())
                .isEqualTo("https://example.com/images/ethiopia-beans.jpg");
        assertThat(response.getStockQuantity()).isEqualTo(100);

        verify(productRepository).findById(1L);
        verify(productRepository).flush();
    }

    @Test
    @DisplayName("존재하지 않는 상품의 정보를 수정하면 예외가 발생한다")
    void updateProductNotFound() {
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("에티오피아 원두")
                .price(20000L)
                .description("꽃향과 산미가 특징인 원두")
                .imageUrl("https://example.com/images/ethiopia-beans.jpg")
                .build();

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> productService.updateProduct(999L, request)
        ).isInstanceOf(ProductNotFoundException.class);

        verify(productRepository, never()).flush();
    }

    @Test
    @DisplayName("상품 재고를 입력한 수량으로 변경한다")
    void updateStock() {
        ProductStockUpdateRequest request =
                ProductStockUpdateRequest.builder()
                        .stockQuantity(75)
                        .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.updateStock(1L, request);

        assertThat(response.getStockQuantity()).isEqualTo(75);
        assertThat(response.getName()).isEqualTo("콜롬비아 원두");
        assertThat(response.getPrice()).isEqualTo(18000L);

        verify(productRepository).findById(1L);
        verify(productRepository).flush();
    }

    @Test
    @DisplayName("존재하지 않는 상품의 재고를 수정하면 예외가 발생한다")
    void updateStockNotFound() {
        ProductStockUpdateRequest request =
                ProductStockUpdateRequest.builder()
                        .stockQuantity(75)
                        .build();

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> productService.updateStock(999L, request)
        ).isInstanceOf(ProductNotFoundException.class);

        verify(productRepository, never()).flush();
    }

    @Test
    @DisplayName("상품을 삭제한다")
    void deleteProduct() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 삭제하면 예외가 발생한다")
    void deleteProductNotFound() {
        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(productRepository).findById(999L);
        verify(productRepository, never()).delete(any(Product.class));
    }
}