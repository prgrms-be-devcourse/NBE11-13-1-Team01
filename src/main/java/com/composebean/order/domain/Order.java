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
import java.util.Optional;

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
            LocalDateTime deliveryExpectedDate,
            LocalDateTime orderedAt
    ) {
        this.email = email;
        this.address = address;
        this.postalCode = postalCode;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.deliveryStatus = deliveryStatus;
        this.deliveryExpectedDate = deliveryExpectedDate;
        this.orderedAt = orderedAt;
    }

    @PrePersist
    private void prePersist() {
        this.orderedAt = LocalDateTime.now();
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public void addOrderItem(OrderItem orderItem) {
        Optional<OrderItem> existingItem = this.orderItems.stream()
                .filter(item -> item.getProduct().getId().equals(orderItem.getProduct().getId()))
                // 2. [핵심] 단, 주소값이 서로 다른 놈이어야 함! (자기 자신은 제외)
                .filter(item -> item != orderItem)
                .findFirst(); // 맨 처음 값 찾기

        if (existingItem.isPresent()) {
            // [중요] 기존 아이템의 필드 3개를 한 번에 업데이트
            existingItem.get().addQuantity(orderItem.getQuantity());

        } else { //대표 주문 영수증에 해당 아이템이 없을 시
            orderItem.assignOrder(this);
            this.orderItems.add(orderItem);
        }

        /*orderItems.add(orderItem);
        orderItem.assignOrder(this);*/
    }

    public void removeOrderItem(OrderItem orderItem) {
        this.orderItems.remove(orderItem);
    }



    //추가
    public void updateTotalPrice(Long newTotal) {
        this.totalPrice = newTotal;
    }

    public void updateDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void updateDeliveryExpectedDate(LocalDateTime deliveryExpectedDate) {
        this.deliveryExpectedDate = deliveryExpectedDate;
    }

}