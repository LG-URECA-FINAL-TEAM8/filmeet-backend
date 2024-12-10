//package com.ureca.filmeet.domain.user.batch;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class UserScoreUpdateScheduler {
//
//    private final JobLauncher jobLauncher;
//    private final Job userScoreUpdateJob;
//
//    //    @Scheduled(cron = "*/10 * * * * ?") // 10초마다 실행
//    @Scheduled(cron = "0 0 3 * * ?")
//    public void runBatchJob() {
//        try {
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//            jobLauncher.run(userScoreUpdateJob, jobParameters);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//}