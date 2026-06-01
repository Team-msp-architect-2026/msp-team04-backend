
package com.moment.momentbackend.notification.service;

import com.moment.momentbackend.bookmark.entity.Bookmark;
import com.moment.momentbackend.bookmark.repository.BookmarkRepository;
import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.notification.dto.NotificationResponse;
import com.moment.momentbackend.notification.entity.Notification;
import com.moment.momentbackend.notification.repository.NotificationRepository;
import com.moment.momentbackend.notification.type.NotificationReferenceType;
import com.moment.momentbackend.notification.type.NotificationType;
import com.moment.momentbackend.program.entity.Program;
import com.moment.momentbackend.program.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProgramRepository programRepository;
    private final BookmarkRepository bookmarkRepository;
    private final NotificationPublisher notificationPublisher;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        validateAuthenticatedUser(userId);

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        validateAuthenticatedUser(userId);

        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();

        return NotificationResponse.from(notification);
    }

    @Transactional
    public void createPaymentDoneNotification(Long userId, Long paymentId, String programTitle) {
        validateAuthenticatedUser(userId);

        createNotificationIfNotExists(
                userId,
                NotificationType.PAYMENT_DONE,
                "결제가 완료되었어요",
                programTitle + " 신청 결제가 확정되었습니다.",
                paymentId,
                NotificationReferenceType.PAYMENT
        );
    }

    @Transactional
    public int createDeadlineSoonNotifications() {
        LocalDate deadlineDate = LocalDate.now().plusDays(3);
        List<Program> programs = programRepository.findRecruitingProgramsByDeadlineDate(deadlineDate);

        int createdCount = 0;
        for (Program program : programs) {
            createdCount += notifyBookmarkedUsers(
                    program,
                    NotificationType.DEADLINE_SOON,
                    "마감 임박 프로그램이 있어요",
                    program.getTitle() + " 모집 마감이 3일 남았습니다."
            );
        }

        return createdCount;
    }

    @Transactional
    public int createRecruitingOpenNotifications() {
        List<Program> programs = programRepository.findRecruitingProgramsForNotification();

        int createdCount = 0;
        for (Program program : programs) {
            createdCount += notifyBookmarkedUsers(
                    program,
                    NotificationType.RECRUITING_OPEN,
                    "모집이 시작된 프로그램이 있어요",
                    program.getTitle() + " 모집이 진행 중입니다."
            );
        }

        return createdCount;
    }

    private int notifyBookmarkedUsers(
            Program program,
            NotificationType type,
            String title,
            String message
    ) {
        List<Bookmark> bookmarks = bookmarkRepository.findByProgramId(program.getId());

        int createdCount = 0;
        for (Bookmark bookmark : bookmarks) {
            boolean created = createNotificationIfNotExists(
                    bookmark.getUserId(),
                    type,
                    title,
                    message,
                    program.getId(),
                    NotificationReferenceType.PROGRAM
            );

            if (created) {
                createdCount++;
            }
        }

        return createdCount;
    }

    private boolean createNotificationIfNotExists(
            Long userId,
            NotificationType type,
            String title,
            String message,
            Long referenceId,
            NotificationReferenceType referenceType
    ) {
        boolean exists = notificationRepository.existsByUserIdAndTypeAndReferenceTypeAndReferenceId(
                userId,
                type,
                referenceType,
                referenceId
        );

        if (exists) {
            return false;
        }

        Notification notification = Notification.create(
                userId,
                type,
                title,
                message,
                referenceId,
                referenceType
        );

        Notification savedNotification = notificationRepository.save(notification);
        notificationPublisher.publish(savedNotification);

        return true;
    }

    private void validateAuthenticatedUser(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}
