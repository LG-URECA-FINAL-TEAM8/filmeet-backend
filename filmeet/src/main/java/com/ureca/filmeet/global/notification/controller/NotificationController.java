package com.ureca.filmeet.global.notification.controller;

import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import com.ureca.filmeet.global.notification.dto.NotificationResponse;
import com.ureca.filmeet.global.notification.service.command.NotificationCommandService;
import com.ureca.filmeet.global.notification.service.query.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ureca.filmeet.domain.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;

    @GetMapping
    public ResponseEntity<ApiResponse<SliceResponseDto<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ApiResponse.ok(notificationQueryService.getNotifications(user, pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(notificationQueryService.getUnreadCount(user));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user
    ) {
        notificationCommandService.markAsRead(notificationId, user);
        return ApiResponse.ok(null);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User user
    ) {
        notificationCommandService.markAllAsRead(user);
        return ApiResponse.ok(null);
    }
}
