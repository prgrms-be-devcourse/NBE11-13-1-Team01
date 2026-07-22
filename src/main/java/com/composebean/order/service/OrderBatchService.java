package com.composebean.order.service;

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
    private final OrderItemRepository orderItemRepository;
    private final DeliveryStatus shipping = DeliveryStatus.SHIPPING;
    private final PaymentStatus paymentStatus = PaymentStatus.PAID;

    @Transactional
    public void autoGroupOrders() {
        // 여기서 날짜기준 & 결제 보류 중 조회로 바꾸기  //여기 부분은 해도 된다.
        LocalDateTime now = LocalDateTime.now();
        // 어제 오후 2시
        LocalDateTime startDate = now.minusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        // 오늘 오후 1시 59분 59초
        LocalDateTime endDate = now.withHour(13).withMinute(59).withSecond(59).withNano(0);
        List<Order> orders = orderRepository.findByOrderedAtBetweenAndPaymentStatus(startDate, endDate, PaymentStatus.PENDING);

        // 이메일, 주소로 그룹핑
        Map<String, List<Order>> groupedMapOrders = orders.stream()
                .collect(Collectors.groupingBy(o->o.getEmail() + "|" + o.getAddress()));

        groupedMapOrders.forEach((key,orderList)->{
            processGroup(orderList);
        });
    }

    //그룹을 하나의 데이터로,
    private void processGroup(List<Order> orderList) {
        // 1. 대표 주문 선정 (첫 번째 주문을 대표로 사용)
        Order representativeOrder = orderList.get(orderList.size() - 1);

        // 2. 나머지 주문(index 1부터)의 아이템들을 대표 주문으로 이동
        for (int i = 0; i < orderList.size() - 1; i++) {
            Order otherOrder = orderList.get(i);

            //기존 아이템 옮기기
            List<OrderItem> itemsToMove = new ArrayList<>(otherOrder.getOrderItems());
            for (OrderItem item : itemsToMove) {
                representativeOrder.addOrderItem(item);
                otherOrder.removeOrderItem(item); // otherOrder 내부에 정의된 제거 메서드 사용
                orderItemRepository.delete(item);
            }

            // 아이템을 옮긴 후, 기존의 껍데기 주문은 삭제 처리
            orderRepository.delete(otherOrder);
        }

        // 3. totalPrice 재계산
        long newTotal = representativeOrder.getOrderItems().stream()
                .mapToLong(OrderItem::getSubtotal)
                .sum();
        representativeOrder.updateTotalPrice(newTotal);

        // paymentStatus 업데이트
        representativeOrder.updatePaymentStatus(paymentStatus);

        // deliveryStatus 업데이트
        representativeOrder.updateDeliveryStatus(shipping);

        // deliveryExpectedDate 업데이트
        LocalDateTime baseDate = representativeOrder.getOrderedAt();
        int days = DeliveryAreaDuration.getDeliveryDaysByAddress(representativeOrder.getAddress());
        representativeOrder.updateDeliveryExpectedDate(baseDate.plusDays(days));
    }

}
