
package com.moment.momentbackend.notification.scheduler;

import com.moment.momentbackend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.scheduler.enabled", havingValue = "true")
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "${notification.scheduler.deadline-soon-cron:0 0 9 * * *}")
    public void createDeadlineSoonNotifications() {
        int createdCount = notificationService.createDeadlineSoonNotifications();
        log.info("마감 D-3 알림 생성 완료. createdCount={}", createdCount);
    }

    @Scheduled(cron = "${notification.scheduler.recruiting-open-cron:0 10 9 * * *}")
    public void createRecruitingOpenNotifications() {
        int createdCount = notificationService.createRecruitingOpenNotifications();
        log.info("모집 오픈 알림 생성 완료. createdCount={}", createdCount);
    }
}
