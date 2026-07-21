package com.composebean.order.service;

import com.composebean.order.domain.Order;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.exception.OrderNotFoundException;
import com.composebean.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        return OrderDetailResponse.from(order);
    }
}
