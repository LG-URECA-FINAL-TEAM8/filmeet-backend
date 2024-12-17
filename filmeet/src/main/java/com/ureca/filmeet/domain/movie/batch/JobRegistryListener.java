package com.ureca.filmeet.domain.movie.batch;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobRegistryListener implements ApplicationListener<ContextRefreshedEvent> {

    private final JobRegistry jobRegistry;
    private final List<Job> jobs;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        jobs.forEach(job -> {
            try {
                jobRegistry.register(new ReferenceJobFactory(job));
                log.info("Registered job: {}", job.getName());
            } catch (Exception e) {
                log.error("Job registration failed for job: {}", job.getName(), e);
            }
        });
    }
}