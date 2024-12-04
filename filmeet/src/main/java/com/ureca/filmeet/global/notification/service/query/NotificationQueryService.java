package com.ureca.filmeet.global.notification.service.query;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import com.ureca.filmeet.global.notification.dto.NotificationResponse;
import com.ureca.filmeet.global.notification.entity.Notification;
import com.ureca.filmeet.global.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {
    private final NotificationRepository notificationRepository;

    public SliceResponseDto<NotificationResponse> getNotifications(
            User user,
            Boolean isRead,  // Boolean 객체로 변경하여 null 허용
            Pageable pageable
    ) {
        Slice<Notification> notifications;

        if (isRead != null) {
            // 읽음/안읽음 필터링이 지정된 경우
            notifications = notificationRepository
                    .findByReceiverAndIsReadOrderByCreatedAtDesc(user, isRead, pageable);
        } else {
            // 모든 알림 조회
            notifications = notificationRepository
                    .findByReceiverOrderByCreatedAtDesc(user, pageable);
        }

        return SliceResponseDto.of(notifications.map(NotificationResponse::from));
    }


    public long getUnreadCount(User user) {
        return notificationRepository.countByReceiverAndIsReadFalse(user);
    }
}
