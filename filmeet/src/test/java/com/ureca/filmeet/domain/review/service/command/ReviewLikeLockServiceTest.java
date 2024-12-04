package com.ureca.filmeet.domain.review.service.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
public class ReviewLikeLockServiceTest {

    @Autowired
    private ReviewLikesCommandService reviewLikesCommandService;

    @Autowired
    private ReviewRepository reviewRepository;

    @DisplayName("좋아요_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void likeCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 138 + i;
            executorService.submit(() -> {
                try {
                    reviewLikesCommandService.reviewLikes(63L, (long) num);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Optional<Review> review = reviewRepository.findById(63L);
        assertThat(review).isPresent();
        assertThat(review.get().getLikeCounts()).isEqualTo(1000);
    }

    @DisplayName("좋아요_취소_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void likeCancelCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch likeLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch cancelLatch = new CountDownLatch(numberOfThreads);

        // 좋아요 작업
        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 128 + i;
            executorService.submit(() -> {
                try {
                    reviewLikesCommandService.reviewLikes(63L, (long) num);
                } finally {
                    likeLatch.countDown();
                }
            });
        }

        // 좋아요 작업 완료 대기
        likeLatch.await();

        // 취소 작업
        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 128 + i;
            executorService.submit(() -> {
                try {
                    reviewLikesCommandService.reviewLikesCancel(63L, (long) num);
                } finally {
                    cancelLatch.countDown();
                }
            });
        }

        // 취소 작업 완료 대기
        cancelLatch.await();

        Optional<Review> review = reviewRepository.findById(63L);
        assertThat(review).isPresent();
        assertThat(review.get().getLikeCounts()).isEqualTo(0);
    }
}
