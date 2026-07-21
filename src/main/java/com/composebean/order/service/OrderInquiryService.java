package com.composebean.order.service;

import com.composebean.order.dto.OrderSummaryResponse;
import com.composebean.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderInquiryService {
    private final OrderRepository orderRepository;

    public Page<OrderSummaryResponse> getOrders(String email, Pageable pageable) {
        return orderRepository.getOrders(email, pageable);
    }
}
