package com.ureca.filmeet.global.notification.service.command;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.notification.entity.Notification;
import com.ureca.filmeet.global.notification.exception.NotificationAccessDeniedException;
import com.ureca.filmeet.global.notification.exception.NotificationNotFoundException;
import com.ureca.filmeet.global.notification.repository.NotificationRepository;
import com.ureca.filmeet.infra.firebase.service.FCMTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.ureca.filmeet.global.notification.entity.NotificationType.COMMENT;
import static com.ureca.filmeet.global.notification.entity.NotificationType.FOLLOW;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService {
    private final FCMTokenService fcmTokenService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    /**
     * 팔로우 알림 발송
     */
    public void sendFollowNotification(User follower, User following) {
        String title = "새로운 팔로워";
        String body = String.format("%s님이 회원님을 팔로우했습니다", follower.getNickname());

        // FCM 메시지에 포함될 추가 데이터
        Map<String, String> data = Map.of(
                "type", "FOLLOW",
                "followerId", String.valueOf(follower.getId()),
                "followerNickname", follower.getNickname(),
                "followerProfileImage", follower.getProfileImage() != null ? follower.getProfileImage() : ""
        );

        fcmTokenService.sendNotificationWithData(following, title, body, data);

        notificationRepository.save(Notification.builder()
                .receiver(following)
                .sender(follower)
                .type(FOLLOW)
                .message(body)
                .contentId(String.valueOf(follower.getId()))
                .build());
    }

    /**
     * 댓글 알림 발송
     */
    public void sendReviewNotification(User commenter, Long contentId, List<User> receivers) {
        String title = "새로운 리뷰";
        String body = String.format("%s님이 리뷰를 작성했습니다", commenter.getNickname());

        // FCM 메시지에 포함될 추가 데이터
        Map<String, String> data = Map.of(
                "type", "REVIEW",
                "contentId", String.valueOf(contentId),
                "commenterId", String.valueOf(commenter.getId())
        );

        receivers.forEach(receiver -> {
            if (!receiver.getId().equals(commenter.getId())) {
                fcmTokenService.sendNotificationWithData(receiver, title, body, data);
            }

            notificationRepository.save(Notification.builder()
                    .receiver(receiver)
                    .sender(commenter)
                    .type(COMMENT)
                    .message(body)
                    .contentId(contentId.toString())
                    .build());
        });


    }

    // 알림 읽음 처리
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        validateReceiver(notification, user);
        notification.markAsRead();
    }

    // 전체 읽음 처리
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications =
                notificationRepository.findByReceiverAndIsReadFalse(user);

        unreadNotifications.forEach(Notification::markAsRead);
    }

    public void deleteNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        validateReceiver(notification, user);
        notificationRepository.delete(notification);
    }

    public void deleteAllNotifications(User user) {
        List<Notification> notifications = notificationRepository.findByReceiver(user);
        notificationRepository.deleteAll(notifications);
    }


    private void validateReceiver(Notification notification, User user) {
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new NotificationAccessDeniedException();
        }
    }


}
