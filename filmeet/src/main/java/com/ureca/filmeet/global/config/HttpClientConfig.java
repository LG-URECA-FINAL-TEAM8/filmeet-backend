package com.ureca.filmeet.global.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10)) // 연결 타임아웃 설정
                .version(HttpClient.Version.HTTP_2)
                .build();
    }
}