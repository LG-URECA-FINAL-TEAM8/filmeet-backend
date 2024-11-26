package com.ureca.filmeet.infra.s3.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.regions.Regions;

@Configuration
public class S3Config {

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2) // 서울 리전 설정
                .build();
    }
}
