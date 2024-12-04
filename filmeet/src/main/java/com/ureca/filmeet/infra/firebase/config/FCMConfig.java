package com.ureca.filmeet.infra.firebase.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FCMConfig {

    @Value("${firebase.config-path}")
    private String firebaseConfigPath; // 환경 변수에서 경로를 가져옵니다.

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials;

        // 배포 환경에서는 환경 변수에 설정된 경로 사용, 로컬에서는 ClassPathResource 사용
        if (firebaseConfigPath.startsWith("classpath:")) {
            // 로컬 환경: classpath에서 파일 읽기
            googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath.substring("classpath:".length())).getInputStream());
        } else {
            // 배포 환경: 환경 변수에서 지정된 경로 사용
            googleCredentials = GoogleCredentials.fromStream(new FileInputStream(firebaseConfigPath));
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();

        // FirebaseApp이 이미 초기화되어 있는지 확인
        FirebaseApp firebaseApp;
        if (FirebaseApp.getApps().isEmpty()) {
            firebaseApp = FirebaseApp.initializeApp(options);
        } else {
            firebaseApp = FirebaseApp.getInstance();
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
