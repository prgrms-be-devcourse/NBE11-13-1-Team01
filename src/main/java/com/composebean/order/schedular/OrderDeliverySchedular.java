package com.composebean.order.schedular;

import com.composebean.order.service.OrderBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliverySchedular {

    private final OrderBatchService orderBatchService;

    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
    public void dailyDeliveryReport() {
        log.info("[일일 주문 배송 처리 시작]");
        orderBatchService.autoGroupOrders();
        log.info("[일일 주문 배송 처리 완료]");
    }
}
