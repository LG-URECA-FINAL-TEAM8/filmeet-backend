package com.ureca.filmeet;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class FilmeetApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));    // EC2에서도 Tomcat 서버의 시간을 서울 시간으로 변경한다.
        SpringApplication.run(FilmeetApplication.class, args);
    }

}