
package com.moment.momentbackend.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.momentbackend.notification.config.NotificationSnsProperties;
import com.moment.momentbackend.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnsNotificationPublisher implements NotificationPublisher {

    private final SnsClient snsClient;
    private final NotificationSnsProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Notification notification) {
        if (!properties.isEnabled()) {
            return;
        }

        if (!StringUtils.hasText(properties.getTopicArn())) {
            log.warn("SNS 알림 발행이 활성화되어 있지만 notification.sns.topic-arn 설정이 비어 있습니다.");
            return;
        }

        try {
            snsClient.publish(PublishRequest.builder()
                    .topicArn(properties.getTopicArn())
                    .subject(notification.getTitle())
                    .message(toMessage(notification))
                    .build());
        } catch (Exception e) {
            log.warn("SNS 알림 발행 실패. notificationId={}, type={}, reason={}",
                    notification.getId(), notification.getType(), e.getMessage());
        }
    }

    private String toMessage(Notification notification) throws JsonProcessingException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("notificationId", notification.getId());
        payload.put("userId", notification.getUserId());
        payload.put("type", notification.getType());
        payload.put("title", notification.getTitle());
        payload.put("body", notification.getMessage());
        payload.put("referenceId", notification.getReferenceId());
        payload.put("referenceType", notification.getReferenceType());
        payload.put("createdAt", notification.getCreatedAt());

        return objectMapper.writeValueAsString(payload);
    }
}
