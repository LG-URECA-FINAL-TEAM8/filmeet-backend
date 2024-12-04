package com.ureca.filmeet.infra.firebase.repository;

import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.infra.firebase.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
    Optional<FCMToken> findByUserAndToken(User user, String token);
    List<FCMToken> findAllByUser(User user);
    void deleteByUserAndToken(User user, String token);
}
