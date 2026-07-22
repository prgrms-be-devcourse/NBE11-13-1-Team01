package com.composebean.order.service;

import com.composebean.order.domain.DeliveryStatus;
import com.composebean.order.domain.Order;
import com.composebean.order.dto.OrderSummaryResponse;
import com.composebean.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderInquiryService {
    private final OrderRepository orderRepository;

    @Transactional
    public Page<OrderSummaryResponse> getOrders(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findAll();
        for(Order order : orders) {
            if(order != null && order.getDeliveryExpectedDate().isBefore(LocalDateTime.now()) ) {
                order.updateDeliveryStatus(DeliveryStatus.DELIVERED);
            }
        }
        return orderRepository.getOrders(email, pageable);
    }
}
