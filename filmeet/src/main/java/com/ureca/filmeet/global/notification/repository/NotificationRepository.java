package com.ureca.filmeet.global.notification.repository;

import com.ureca.filmeet.global.notification.entity.Notification;
import com.ureca.filmeet.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림 조회 추가
    Slice<Notification> findByReceiverOrderByCreatedAtDesc(User receiver, Pageable pageable);

    // 읽지 않은 알림 조회 추가
    List<Notification> findByReceiverAndIsReadFalse(User receiver);

    // 읽지 않은 알림 개수 조회 추가
    long countByReceiverAndIsReadFalse(User receiver);

    // 읽음 상태에 따른 조회 추가
    Slice<Notification> findByReceiverAndIsReadOrderByCreatedAtDesc(User receiver, boolean isRead, Pageable pageable);

    // 알림 조회 추가
    List<Notification> findByReceiver(User receiver);
}
