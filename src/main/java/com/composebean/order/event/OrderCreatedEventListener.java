package com.composebean.order.event;

import com.composebean.global.slack.SlackClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

    private final SlackClient slackClient;
    private static final DateTimeFormatter ORDER_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCreatedEvent event) {
        try {
            slackClient.sendOrderNotification(createMessage(event));

            log.info(
                    "Slack 주문 알림 전송 성공. orderId={}",
                    event.orderId()
            );
        } catch (Exception e) {
            log.error(
                    "Slack 주문 알림 전송 실패. orderId={}",
                    event.orderId(),
                    e
            );
        }
    }

    private String createMessage(OrderCreatedEvent event) {
        String itemLines = event.items().stream()
                .map(item -> "- %s %d개 / %,d원"
                        .formatted(
                                item.productName(),
                                item.quantity(),
                                item.subtotal()
                        ))
                .collect(Collectors.joining("\n"));

        return """
                [Compose Bean 새 주문 접수]

                주문 번호: %d
                주문자 이메일: %s
                배송지: %s (%s)

                주문 상품:
                %s

                총 주문 금액: %,d원
                주문 시각: %s
                """.formatted(
                event.orderId(),
                event.email(),
                event.address(),
                event.postalCode(),
                itemLines,
                event.totalPrice(),
                event.orderedAt().format(ORDER_TIME_FORMATTER)
        );
    }
}