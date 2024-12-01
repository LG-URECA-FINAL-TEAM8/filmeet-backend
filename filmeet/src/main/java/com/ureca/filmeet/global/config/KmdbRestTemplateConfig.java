package com.ureca.filmeet.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class KmdbRestTemplateConfig {

    @Bean
    public RestTemplate kmdbRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // MappingJackson2HttpMessageConverter 설정
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(List.of(
                org.springframework.http.MediaType.TEXT_HTML, // KMDB API가 반환하는 text/html 처리
                org.springframework.http.MediaType.APPLICATION_JSON
        ));

        // RestTemplate에 커스텀 컨버터 추가
        restTemplate.getMessageConverters().add(0, jsonConverter);

        return restTemplate;
    }
}