package com.ureca.filmeet.domain.movie.batch;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MovieRecommendationJobScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    //    @Scheduled(cron = "*/10 * * * * ?")
    @Scheduled(cron = "0 0 4 * * ?")
    public void runMovieRecommendationJob() {
        try {
            Job job = jobRegistry.getJob("updateMovieRecommendationJob");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runDate", LocalDate.now().toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info("Running job: updateMovieRecommendationJob with parameters: {}", jobParameters);
            jobLauncher.run(job, jobParameters);

            log.info("Successfully completed job: updateMovieRecommendationJob");

        } catch (Exception e) {
            log.error("Failed to run updateMovieRecommendationJob", e);
        }
    }
}