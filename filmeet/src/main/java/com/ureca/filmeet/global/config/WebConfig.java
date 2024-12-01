//package com.ureca.filmeet.global.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
//                        .allowedOrigins("http://localhost:5173") // React 앱의 주소
//                        .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
//                        .allowedHeaders("*") // 허용할 헤더
//                        .allowCredentials(true); // 자격 증명 허용 (예: 쿠키)
//            }
//        };
//    }
//}
