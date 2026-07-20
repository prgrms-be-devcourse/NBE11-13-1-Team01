package com.composebean.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false)
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryStatus deliveryStatus;

    @Column(nullable = false)
    private LocalDateTime deliveryExpectedDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime orderedAt;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL
    )
    private final List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(
            String email,
            String address,
            String postalCode,
            Long totalPrice,
            PaymentStatus paymentStatus,
            DeliveryStatus deliveryStatus,
            LocalDateTime deliveryExpectedDate
    ) {
        this.email = email;
        this.address = address;
        this.postalCode = postalCode;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.deliveryStatus = deliveryStatus;
        this.deliveryExpectedDate = deliveryExpectedDate;
    }

    @PrePersist
    private void prePersist() {
        this.orderedAt = LocalDateTime.now();
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.assignOrder(this);
    }
}