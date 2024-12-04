package com.ureca.filmeet.infra.firebase.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FCMConfig {

    @Value("${firebase.config-path:}")
    private String firebaseConfigPath;

    @Value("${FIREBASE_CONFIG:}")
    private String firebaseConfigJson;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        try {
            GoogleCredentials googleCredentials;

            // local 환경: classpath에서 파일 읽기
            if (!StringUtils.isEmpty(firebaseConfigPath) && firebaseConfigPath.startsWith("classpath:")) {
                googleCredentials = GoogleCredentials
                        .fromStream(new ClassPathResource(firebaseConfigPath.substring("classpath:".length())).getInputStream());
            }
            // dev 환경: 환경 변수에서 JSON 직접 읽기
            else if (!StringUtils.isEmpty(firebaseConfigJson)) {
                googleCredentials = GoogleCredentials
                        .fromStream(new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8)));
            }
            else {
                throw new IllegalStateException("Firebase 설정을 찾을 수 없습니다.");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .build();

            // FirebaseApp이 이미 초기화되어 있는지 확인
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            return FirebaseMessaging.getInstance();
        } catch (IOException e) {
            log.error("Firebase 초기화 실패: ", e);
            throw e;
        }
    }
}