package com.composebean.order.service;

import com.composebean.global.exception.BusinessException;
import com.composebean.global.exception.ErrorCode;
import com.composebean.order.domain.*;
import com.composebean.order.repository.OrderItemRepository;
import com.composebean.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderBatchService {

    private final OrderRepository orderRepository;

    @Transactional
    public void autoGroupOrders() {
        // 여기서 날짜기준 & 결제 보류 중 조회로 바꾸기  //여기 부분은 해도 된다.
        LocalDateTime now = LocalDateTime.now();
        // 어제 오후 2시
        LocalDateTime startDate = now.minusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        // 오늘 오후 1시 59분 59초
        LocalDateTime endDate = now.withHour(13).withMinute(59).withSecond(59).withNano(0);
        List<Order> orders = orderRepository.findByOrderedAtBetweenAndPaymentStatus(startDate, endDate, PaymentStatus.PAID);

        // 이메일, 주소로 그룹핑
        Map<String, List<Order>> groupedMapOrders = orders.stream()
                .collect(Collectors.groupingBy(o->o.getEmail() + "|" + o.getAddress()));

        groupedMapOrders.forEach((key,orderList)->{
            processGroup(orderList);
        });
    }

    private void processGroup(List<Order> orderList) {
        if (orderList == null || orderList.isEmpty()) {
            return;
        }

        Order representativeOrder = orderList.get(orderList.size() - 1);
        Order newOrder = copyOrder(representativeOrder);

        try {
            for (Order order : orderList) {
                order.delete();

                List<OrderItem> itemsToMove =
                        new ArrayList<>(order.getOrderItems());

                for (OrderItem item : itemsToMove) {
                    newOrder.addOrderItem(item);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        long newTotal = newOrder.getOrderItems().stream()
                .mapToLong(OrderItem::getSubtotal)
                .sum();

        newOrder.updateTotalPrice(newTotal);
        newOrder.updatePaymentStatus(PaymentStatus.PAID);
        newOrder.updateDeliveryStatus(DeliveryStatus.SHIPPING);

        orderRepository.save(newOrder);
    }

    public Order copyOrder(Order original) {
        // 외부에서 빌더로 복사본 생성 (id는 제외됨)
        return Order.builder()
                .email(original.getEmail())
                .address(original.getAddress())
                .postalCode(original.getPostalCode())
                .totalPrice(original.getTotalPrice())
                .paymentStatus(original.getPaymentStatus())
                .deliveryStatus(original.getDeliveryStatus())
                .deliveryExpectedDate(original.getDeliveryExpectedDate())
                .orderedAt(original.getOrderedAt()) // 복사된 시점 혹은 original.getOrderedAt()
                .build();
    }

}
