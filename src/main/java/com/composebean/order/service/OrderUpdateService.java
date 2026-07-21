package com.composebean.order.service;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
import com.composebean.order.domain.Order;
import com.composebean.order.dto.DeliveryStatusUpdateRequest;
import com.composebean.order.dto.OrderDetailResponse;
import com.composebean.order.exception.OrderNotFoundException;
import com.composebean.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OrderUpdateService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderDetailResponse updateDeliveryStatus(DeliveryStatusUpdateRequest dto, Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        order.updateDeliveryStatus(dto.getDeliveryStatus());
        return orderRepository.getOrderDetail(orderId).orElseThrow(()->new RuntimeException("해당 아이디 없음!"));
    }
}
