package com.composebean.order.repository;

import com.composebean.order.domain.Order;
import com.composebean.order.domain.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom{
    // orderedAt이 startDate와 endDate 사이인 주문들을 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE) // DB 레코드에 락을 겁니다.
    List<Order> findByOrderedAtBetweenAndPaymentStatus(LocalDateTime start, LocalDateTime end, PaymentStatus paymentStatus);

    List<Order> findAllByDeletedAtIsNull();
}
