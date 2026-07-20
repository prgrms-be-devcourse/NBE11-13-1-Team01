package com.composebean.order.domain;

import com.composebean.product.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long unitPrice;

    @Column(nullable = false)
    private Long subtotal;

    @Builder
    public OrderItem(
            Order order,
            Product product,
            Integer quantity,
            Long unitPrice,
            Long subtotal
    ) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

    // 기존에 쓰던 수량만 더하는 메서드도 동일 원칙 적용
    public void addQuantity(Integer quantity) {
        this.quantity += quantity;
        this.subtotal = this.unitPrice * this.quantity;
    }
}