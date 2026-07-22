package com.composebean.global.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class SlackClient {

    private final SlackProperties slackProperties;
    private final RestClient restClient;

    public SlackClient(SlackProperties slackProperties) {
        this.slackProperties = slackProperties;
        this.restClient = RestClient.builder()
                .baseUrl("https://slack.com/api")
                .build();
    }

    public void sendOrderNotification(String message) {
        if (slackProperties.botToken() == null
                || slackProperties.botToken().isBlank()
                || slackProperties.orderReceiverId() == null
                || slackProperties.orderReceiverId().isBlank()) {

            log.warn("Slack 설정이 없어 주문 알림을 전송하지 않습니다.");
            return;
        }

        SlackMessageResponse response = restClient.post()
                .uri("/chat.postMessage")
                .header(
                        "Authorization",
                        "Bearer " + slackProperties.botToken()
                )
                .body(Map.of(
                        "channel", slackProperties.orderReceiverId(),
                        "text", message
                ))
                .retrieve()
                .body(SlackMessageResponse.class);

        if (response == null || !response.ok()) {
            String error = response == null
                    ? "empty_response"
                    : response.error();

            throw new IllegalStateException(
                    "Slack 메시지 전송 실패: " + error
            );
        }
    }
}