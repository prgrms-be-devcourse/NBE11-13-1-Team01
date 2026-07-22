package com.composebean.order.event;

import com.composebean.global.slack.SlackClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderCreatedEventListenerTest {

    @Mock
    private SlackClient slackClient;

    @InjectMocks
    private OrderCreatedEventListener listener;

    @Test
    @DisplayName("주문 생성 이벤트를 받으면 Slack 주문 알림을 전송한다")
    void sendOrderNotification() {
        OrderCreatedEvent event = createEvent();

        listener.handle(event);

        ArgumentCaptor<String> messageCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(slackClient)
                .sendOrderNotification(messageCaptor.capture());

        assertThat(messageCaptor.getValue())
                .contains("Compose Bean 새 주문 접수")
                .contains("customer@example.com")
                .contains("Colombia Narino")
                .contains("10,000원");
    }

    @Test
    @DisplayName("Slack 전송에 실패해도 예외를 외부로 전파하지 않는다")
    void ignoreSlackFailure() {
        OrderCreatedEvent event = createEvent();

        doThrow(new IllegalStateException("Slack API 오류"))
                .when(slackClient)
                .sendOrderNotification(org.mockito.ArgumentMatchers.anyString());

        assertThatCode(() -> listener.handle(event))
                .doesNotThrowAnyException();
    }

    private OrderCreatedEvent createEvent() {
        return new OrderCreatedEvent(
                1L,
                "customer@example.com",
                "서울특별시 강남구 테헤란로 123",
                "06234",
                10000L,
                LocalDateTime.of(2026, 7, 22, 10, 0),
                List.of(
                        new OrderCreatedEvent.OrderItemInfo(
                                "Colombia Narino",
                                2,
                                5000L,
                                10000L
                        )
                )
        );
    }
}