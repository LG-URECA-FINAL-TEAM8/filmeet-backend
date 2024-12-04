package com.ureca.filmeet.infra.firebase.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.infra.firebase.entity.FCMToken;
import com.ureca.filmeet.infra.firebase.exception.FCMSendFailedException;
import com.ureca.filmeet.infra.firebase.exception.FCMTokenNotFoundException;
import com.ureca.filmeet.infra.firebase.exception.InvalidFCMTokenException;
import com.ureca.filmeet.infra.firebase.repository.FCMTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FCMTokenService {
    private final FCMTokenRepository fcmTokenRepository;
    private final FirebaseMessaging firebaseMessaging;

    public void saveToken(User user, String token) {
        // 토큰이 이미 존재하는지 확인
        fcmTokenRepository.findByUserAndToken(user, token)
                .ifPresentOrElse(
                        existingToken -> existingToken.updateToken(token),
                        () -> fcmTokenRepository.save(FCMToken.builder()
                                .user(user)
                                .token(token)
                                .build())
                );
    }

    public void removeToken(User user, String token) {
        fcmTokenRepository.deleteByUserAndToken(user, token);
    }

    public void sendNotificationWithData(User receiver, String title, String body, Map<String, String> data) {
        List<FCMToken> tokens = fcmTokenRepository.findAllByUser(receiver);

        if (tokens.isEmpty()) {
            throw new FCMTokenNotFoundException();
        }

        List<String> invalidTokens = new ArrayList<>();

        for (FCMToken token : tokens) {
            try {
                Message message = Message.builder()
                        .setToken(token.getToken())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .putAllData(data)
                        .build();

                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                invalidTokens.add(token.getToken());
                log.error("FCM send failed for token: {}", token.getToken(), e);
            }
        }

        // 유효하지 않은 토큰 삭제
        if (!invalidTokens.isEmpty()) {
            invalidTokens.forEach(token ->
                    fcmTokenRepository.deleteByUserAndToken(receiver, token));
        }

        // 모든 토큰이 유효하지 않은 경우
        if (invalidTokens.size() == tokens.size()) {
            throw new FCMSendFailedException();
        }
    }
}
