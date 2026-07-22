package com.composebean.product.repository;

import com.composebean.global.config.QueryDSLConfig;
import com.composebean.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDSLConfig.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("삭제되지 않은 상품만 목록으로 조회한다")
    void findAllByDeletedAtIsNull() {
        Product activeProduct = createProduct(
                "콜롬비아 원두",
                18000L
        );

        Product deletedProduct = createProduct(
                "에티오피아 원두",
                20000L
        );
        deletedProduct.delete();

        productRepository.save(activeProduct);
        productRepository.save(deletedProduct);
        productRepository.flush();

        List<Product> products =
                productRepository
                        .findAllByDeletedAtIsNull();

        assertThat(products)
                .extracting(Product::getName)
                .contains("콜롬비아 원두")
                .doesNotContain("에티오피아 원두");
    }

    @Test
    @DisplayName("상품명 검색에서 삭제된 상품을 제외한다")
    void findByNameContainingIgnoreCaseAndDeletedAtIsNull() {
        Product activeProduct = createProduct(
                "콜롬비아 원두",
                18000L
        );

        Product deletedProduct = createProduct(
                "에티오피아 원두",
                20000L
        );
        deletedProduct.delete();

        productRepository.save(activeProduct);
        productRepository.save(deletedProduct);
        productRepository.flush();

        List<Product> products =
                productRepository
                        .findByNameContainingIgnoreCaseAndDeletedAtIsNull(
                                "원두"
                        );

        assertThat(products)
                .extracting(Product::getName)
                .containsExactly("콜롬비아 원두");
    }

    @Test
    @DisplayName("삭제되지 않은 상품은 ID로 조회할 수 있다")
    void findActiveProductById() {
        Product product = productRepository.save(
                createProduct(
                        "콜롬비아 원두",
                        18000L
                )
        );

        Optional<Product> foundProduct =
                productRepository
                        .findByIdAndDeletedAtIsNull(
                                product.getId()
                        );

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName())
                .isEqualTo("콜롬비아 원두");
    }

    @Test
    @DisplayName("삭제된 상품은 ID로 조회할 수 없다")
    void doNotFindDeletedProductById() {
        Product product = createProduct(
                "콜롬비아 원두",
                18000L
        );
        product.delete();

        Product savedProduct =
                productRepository.save(product);
        productRepository.flush();

        Optional<Product> foundProduct =
                productRepository
                        .findByIdAndDeletedAtIsNull(
                                savedProduct.getId()
                        );

        assertThat(foundProduct).isEmpty();

        assertThat(
                productRepository.findById(
                        savedProduct.getId()
                )
        ).isPresent();
    }

    private Product createProduct(
            String name,
            Long price
    ) {
        return Product.builder()
                .name(name)
                .price(price)
                .description(name + " 설명")
                .imageUrl(null)
                .stockQuantity(100)
                .build();
    }
}