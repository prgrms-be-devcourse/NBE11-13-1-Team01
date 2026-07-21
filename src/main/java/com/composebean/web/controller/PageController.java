package com.composebean.web.controller;

import com.composebean.order.dto.OrderCreateResponse;
import com.composebean.order.service.OrderCreateService;
import com.composebean.order.service.OrderDetailService;
import com.composebean.product.dto.ProductCreateRequest;
import com.composebean.product.dto.ProductStockUpdateRequest;
import com.composebean.product.dto.ProductUpdateRequest;
import com.composebean.product.service.ProductService;
import com.composebean.web.dto.OrderForm;
import com.composebean.web.dto.ProductForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final ProductService productService;
    private final OrderCreateService orderCreateService;
    private final OrderDetailService orderDetailService;

    @GetMapping({"/", "/order"})
    public String orderPage(
            @RequestParam(required = false) String name,
            Model model
    ) {
        model.addAttribute("products", productService.getProducts(name).getProducts());
        model.addAttribute("searchName", name);
        model.addAttribute("orderForm", new OrderForm());

        return "order";
    }

    @PostMapping("/order")
    public String createOrder(@ModelAttribute OrderForm orderForm) {
        OrderCreateResponse response = orderCreateService.createOrder(
                orderForm.toRequest()
        );

        return "redirect:/receipt/" + response.getOrderId();
    }

    @GetMapping("/receipt/{orderId}")
    public String receiptPage(
            @PathVariable Long orderId,
            Model model
    ) {
        model.addAttribute("order", orderDetailService.getOrder(orderId));

        return "receipt";
    }

    @GetMapping("/orders")
    public String orderListPage(
            @RequestParam(required = false) String email,
            Model model
    ) {
        model.addAttribute("email", email);
        model.addAttribute("orders", List.of());

        return "order-list";
    }

    @GetMapping("/admin/products")
    public String productAdminPage(Model model) {
        model.addAttribute("products", productService.getProducts(null).getProducts());
        model.addAttribute("productForm", new ProductForm());

        return "product-admin";
    }

    @PostMapping("/admin/products")
    public String createProduct(@ModelAttribute ProductForm productForm) {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(productForm.getName())
                .price(productForm.getPrice())
                .description(productForm.getDescription())
                .imageUrl(productForm.getImageUrl())
                .stockQuantity(productForm.getStockQuantity())
                .build();

        productService.createProduct(request);

        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{productId}")
    public String updateProduct(
            @PathVariable Long productId,
            @ModelAttribute ProductForm productForm
    ) {
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name(productForm.getName())
                .price(productForm.getPrice())
                .description(productForm.getDescription())
                .imageUrl(productForm.getImageUrl())
                .build();

        productService.updateProduct(productId, request);

        ProductStockUpdateRequest stockRequest = ProductStockUpdateRequest.builder()
                .stockQuantity(productForm.getStockQuantity())
                .build();

        productService.updateStock(productId, stockRequest);

        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{productId}/stock")
    public String updateStock(
            @PathVariable Long productId,
            @RequestParam Integer stockQuantity
    ) {
        ProductStockUpdateRequest request = ProductStockUpdateRequest.builder()
                .stockQuantity(stockQuantity)
                .build();

        productService.updateStock(productId, request);

        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{productId}/delete")
    public String deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);

        return "redirect:/admin/products";
    }
}
