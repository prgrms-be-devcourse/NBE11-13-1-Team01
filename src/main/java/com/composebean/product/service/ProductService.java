package com.composebean.product.service;

import com.composebean.product.domain.Product;
import com.composebean.product.dto.ProductCreateRequest;
import com.composebean.product.dto.ProductListResponse;
import com.composebean.product.dto.ProductResponse;
import com.composebean.product.dto.ProductStockUpdateRequest;
import com.composebean.product.dto.ProductUpdateRequest;
import com.composebean.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductListResponse getProducts(String name) {
        List<Product> products;

        if (name == null || name.isBlank()) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByNameContainingIgnoreCase(name);
        }

        return ProductListResponse.from(products);
    }

    public ProductResponse getProduct(Long productId) {
        Product product = findProduct(productId);

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = request.toEntity();
        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(
            Long productId,
            ProductUpdateRequest request
    ) {
        Product product = findProduct(productId);

        product.update(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getImageUrl()
        );

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateStock(
            Long productId,
            ProductStockUpdateRequest request
    ) {
        Product product = findProduct(productId);

        product.updateStock(request.getStockQuantity());

        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);

        productRepository.delete(product);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "상품을 찾을 수 없습니다. productId=" + productId
                ));
    }
}