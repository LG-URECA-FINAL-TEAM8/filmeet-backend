package com.ureca.filmeet.domain.movie.batch;

import com.ureca.filmeet.domain.movie.entity.MovieRecommendation;
import com.ureca.filmeet.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class MovieRecommendationStep {

    private final JobRepository jobRepository;
    private final MovieRecommendationReader reader;
    private final MovieRecommendationProcessor processor;
    private final MovieRecommendationWriter writer;

    @Bean
    public Step updateMovieRecommendationStep(PlatformTransactionManager transactionManager,
                                              MovieBatchChunkListener movieBatchChunkListener) {
        return new StepBuilder("updateMovieRecommendationStep", jobRepository)
                .<User, List<MovieRecommendation>>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(movieBatchChunkListener)
                .build();
    }
}
