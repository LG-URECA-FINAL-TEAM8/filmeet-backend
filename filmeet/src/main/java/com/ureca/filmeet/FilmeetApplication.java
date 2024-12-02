package com.ureca.filmeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FilmeetApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilmeetApplication.class, args);
	}

}