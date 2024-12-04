package com.ureca.filmeet.global.notification.dto;

import com.ureca.filmeet.global.notification.entity.Notification;
import com.ureca.filmeet.global.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String message,
        NotificationType type,
        UserResponse sender,
        String contentId,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                UserResponse.from(notification.getSender()),
                notification.getContentId(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}

