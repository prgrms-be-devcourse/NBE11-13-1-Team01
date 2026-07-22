package com.composebean.product.service;

import com.composebean.global.file.ImageStorageService;
import com.composebean.product.domain.Product;
import com.composebean.product.dto.ProductCreateRequest;
import com.composebean.product.dto.ProductListResponse;
import com.composebean.product.dto.ProductResponse;
import com.composebean.product.dto.ProductStockUpdateRequest;
import com.composebean.product.dto.ProductUpdateRequest;
import com.composebean.product.exception.ProductNotFoundException;
import com.composebean.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;

    public ProductListResponse getProducts(String name) {
        List<Product> products;

        if (name == null || name.isBlank()) {
            products =
                    productRepository
                            .findAllByDeletedAtIsNull();
        } else {
            products =
                    productRepository
                            .findByNameContainingIgnoreCaseAndDeletedAtIsNull(
                                    name
                            );
        }

        return ProductListResponse.from(products);
    }

    public ProductResponse getProduct(Long productId) {
        Product product = findProduct(productId);

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse createProduct(
            ProductCreateRequest request
    ) {
        String imageUrl =
                saveImage(request.getImageFile());

        Product product = request.toEntity(imageUrl);
        Product savedProduct =
                productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(
            Long productId,
            ProductUpdateRequest request
    ) {
        Product product = findProduct(productId);

        String imageUrl = product.getImageUrl();

        if (hasImage(request.getImageFile())) {
            imageUrl = imageStorageService.store(
                    request.getImageFile()
            );
        } else if (request.isDeleteImage()) {
            imageUrl = null;
        }

        product.update(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                imageUrl
        );

        productRepository.flush();

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateStock(
            Long productId,
            ProductStockUpdateRequest request
    ) {
        Product product = findProduct(productId);

        product.updateStock(
                request.getStockQuantity()
        );

        productRepository.flush();

        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);

        product.delete();
    }

    private String saveImage(
            MultipartFile imageFile
    ) {
        if (!hasImage(imageFile)) {
            return null;
        }

        return imageStorageService.store(imageFile);
    }

    private boolean hasImage(
            MultipartFile imageFile
    ) {
        return imageFile != null
                && !imageFile.isEmpty();
    }

    private Product findProduct(Long productId) {
        return productRepository
                .findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(
                        ProductNotFoundException::new
                );
    }
}