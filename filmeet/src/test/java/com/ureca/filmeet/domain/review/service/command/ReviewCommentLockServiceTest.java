package com.ureca.filmeet.domain.review.service.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.review.dto.request.CreateCommentRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateCommentResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
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
public class ReviewCommentLockServiceTest {

    @Autowired
    private ReviewCommentCommandService reviewCommentCommandService;

    @Autowired
    private ReviewRepository reviewRepository;

    @DisplayName("댓글 저장 - 댓글_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void commentCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 138 + i;
            CreateCommentRequest createCommentRequest = new CreateCommentRequest(63L, (long) num, "내용" + num);
            executorService.submit(() -> {
                try {
                    reviewCommentCommandService.createComment(createCommentRequest);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Optional<Review> review = reviewRepository.findById(63L);
        assertThat(review).isPresent();
        assertThat(review.get().getCommentCounts()).isEqualTo(1000);
    }

    @DisplayName("댓글 삭제 - 댓글_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void deleteCommentCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch likeLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch cancelLatch = new CountDownLatch(numberOfThreads);
        List<Long> commentIds = new CopyOnWriteArrayList<>();

        for (int i = 1; i <= numberOfThreads; i++) {
            int num = 128 + i;
            CreateCommentRequest createCommentRequest = new CreateCommentRequest(63L, (long) num, "내용");
            executorService.submit(() -> {
                try {
                    CreateCommentResponse comment = reviewCommentCommandService.createComment(createCommentRequest);
                    commentIds.add(comment.reviewCommentId());
                } finally {
                    likeLatch.countDown();
                }
            });
        }

        likeLatch.await();

        for (int i = 1; i <= numberOfThreads; i++) {
            int num = i - 1;
            executorService.submit(() -> {
                try {
                    reviewCommentCommandService.deleteComment(63L, commentIds.get(num));
                } finally {
                    cancelLatch.countDown();
                }
            });
        }

        cancelLatch.await();

        Optional<Review> review = reviewRepository.findById(63L);
        assertThat(review).isPresent();
        assertThat(review.get().getCommentCounts()).isEqualTo(0);
    }
}
