
package com.moment.momentbackend.notification;

import com.moment.momentbackend.global.exception.CustomException;
import com.moment.momentbackend.global.exception.ErrorCode;
import com.moment.momentbackend.notification.entity.Notification;
import com.moment.momentbackend.notification.repository.NotificationRepository;
import com.moment.momentbackend.notification.service.NotificationPublisher;
import com.moment.momentbackend.notification.service.NotificationService;
import com.moment.momentbackend.notification.type.NotificationReferenceType;
import com.moment.momentbackend.notification.type.NotificationType;
import com.moment.momentbackend.program.repository.ProgramRepository;
import com.moment.momentbackend.bookmark.repository.BookmarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private ProgramRepository programRepository;
    private BookmarkRepository bookmarkRepository;
    private NotificationPublisher notificationPublisher;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        programRepository = mock(ProgramRepository.class);
        bookmarkRepository = mock(BookmarkRepository.class);
        notificationPublisher = mock(NotificationPublisher.class);

        notificationService = new NotificationService(
                notificationRepository,
                programRepository,
                bookmarkRepository,
                notificationPublisher
        );
    }

    @Test
    void getNotificationsWithoutUserThrowsUnauthorized() {
        assertThatThrownBy(() -> notificationService.getNotifications(null))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Test
    void markAsReadWithoutUserThrowsUnauthorized() {
        assertThatThrownBy(() -> notificationService.markAsRead(null, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    @Test
    void markAsReadMissingNotificationThrowsNotFound() {
        when(notificationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOTIFICATION_NOT_FOUND);
    }

    @Test
    void createPaymentDoneNotificationSavesAndPublishes() {
        Notification saved = Notification.create(
                1L,
                NotificationType.PAYMENT_DONE,
                "결제가 완료되었어요",
                "테스트 프로그램 신청 결제가 확정되었습니다.",
                10L,
                NotificationReferenceType.PAYMENT
        );

        when(notificationRepository.existsByUserIdAndTypeAndReferenceTypeAndReferenceId(
                1L,
                NotificationType.PAYMENT_DONE,
                NotificationReferenceType.PAYMENT,
                10L
        )).thenReturn(false);
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        notificationService.createPaymentDoneNotification(1L, 10L, "테스트 프로그램");

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(notificationPublisher, times(1)).publish(saved);
    }

    @Test
    void createPaymentDoneNotificationSkipsDuplicatedNotification() {
        when(notificationRepository.existsByUserIdAndTypeAndReferenceTypeAndReferenceId(
                1L,
                NotificationType.PAYMENT_DONE,
                NotificationReferenceType.PAYMENT,
                10L
        )).thenReturn(true);

        notificationService.createPaymentDoneNotification(1L, 10L, "테스트 프로그램");

        verify(notificationRepository, never()).save(any(Notification.class));
        verify(notificationPublisher, never()).publish(any(Notification.class));
    }
}
