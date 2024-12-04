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

    public SliceResponseDto<NotificationResponse> getNotifications(User user, Pageable pageable) {
        Slice<Notification> notifications = notificationRepository
                .findByReceiverOrderByCreatedAtDesc(user, pageable);

        return SliceResponseDto.of(notifications.map(NotificationResponse::from));
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByReceiverAndIsReadFalse(user);
    }
}
