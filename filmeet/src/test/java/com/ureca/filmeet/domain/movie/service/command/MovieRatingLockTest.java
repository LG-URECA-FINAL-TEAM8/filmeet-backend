package com.ureca.filmeet.domain.movie.service.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.dto.request.DeleteMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.EvaluateMovieRatingRequest;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
public class MovieRatingLockTest {

    @Autowired
    private MovieRatingsCommandService movieRatingsCommandService;

    @Autowired
    private MovieRepository movieRepository;

    @DisplayName("평점_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void ratingCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 79 + i;
            BigDecimal ratingScore = BigDecimal.valueOf((num % 10) * 0.5 + 0.5);
            System.out.println("ratingScore: " + ratingScore);
            EvaluateMovieRatingRequest evaluateMovieRatingRequest = new EvaluateMovieRatingRequest(
                    1L,
                    ratingScore
            );
            executorService.submit(() -> {
                try {
                    movieRatingsCommandService.evaluateMovieRating(evaluateMovieRatingRequest, (long) num);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Optional<Movie> movie = movieRepository.findById(1L);
        assertThat(movie).isPresent();
        assertThat(movie.get().getRatingCounts()).isEqualTo(1000);
    }

    @DisplayName("평점_삭제_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void ratingDeleteCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch likeLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch cancelLatch = new CountDownLatch(numberOfThreads);

        // 좋아요 작업
        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 79 + i;
            EvaluateMovieRatingRequest evaluateMovieRatingRequest = new EvaluateMovieRatingRequest(1L,
                    BigDecimal.valueOf(4.5));
            executorService.submit(() -> {
                try {
                    movieRatingsCommandService.evaluateMovieRating(evaluateMovieRatingRequest, (long) num);
                } finally {
                    likeLatch.countDown();
                }
            });
        }

        // 좋아요 작업 완료 대기
        likeLatch.await();

        // 취소 작업
        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 79 + i;
            DeleteMovieRatingRequest deleteMovieRatingRequest = new DeleteMovieRatingRequest(1L, (long) num);
            executorService.submit(() -> {
                try {
                    movieRatingsCommandService.deleteMovieRating(deleteMovieRatingRequest);
                } finally {
                    cancelLatch.countDown();
                }
            });
        }

        // 취소 작업 완료 대기
        cancelLatch.await();

        Optional<Movie> movie = movieRepository.findById(1L);
        assertThat(movie).isPresent();
        assertThat(movie.get().getRatingCounts()).isEqualTo(0);
    }
}
