package com.composebean.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // 'order'는 SQL 예약어이므로 보통 'orders'로 명명
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotNull
    @NotEmpty
    @Column(nullable = false, length = 100)
    private String email;

    @NotNull
    @NotEmpty
    @Column(nullable = false, length = 255)
    private String address;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 20)
    @Column(nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false)
    private Long totalPrice;

    @Column(nullable = false, length = 20)
    private String paymentStatus;

    @Column(nullable = false, length = 20)
    private String deliveryStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime deliveryExpectedDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();
}