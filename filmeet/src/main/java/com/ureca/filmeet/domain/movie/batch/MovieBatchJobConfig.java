package com.ureca.filmeet.domain.movie.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MovieBatchJobConfig {

    private final JobRepository jobRepository;

    @Bean
    public Job updateMovieRecommendationJob(Step updateMovieRecommendationStep) {
        return new JobBuilder("updateMovieRecommendationJob", jobRepository)
                .start(updateMovieRecommendationStep)
                .build();
    }
}
