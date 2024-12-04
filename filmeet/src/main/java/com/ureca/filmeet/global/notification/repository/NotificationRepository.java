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
    Slice<Notification> findByReceiverOrderByCreatedAtDesc(User receiver, Pageable pageable);
    List<Notification> findByReceiverAndIsReadFalse(User receiver);
    long countByReceiverAndIsReadFalse(User receiver);
}
