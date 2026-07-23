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

@Service
@RequiredArgsConstructor
public class OrderUpdateService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderDetailResponse updateDeliveryStatus(
            DeliveryStatusUpdateRequest dto,
            Long orderId
    ) {
        Order order = orderRepository
                .findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(OrderNotFoundException::new);

        order.updateDeliveryStatus(dto.getDeliveryStatus());

        return orderRepository.getOrderDetail(orderId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.ORDER_NOT_FOUND)
                );
    }
}