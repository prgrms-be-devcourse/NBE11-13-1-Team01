package com.composebean.order.repository;

import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.dto.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryCustom {

    Page<OrderSummaryResponse> getOrders(String email, Pageable pageable);

    Optional<OrderDetailResponse> getOrderDetail(Long orderId);
}
