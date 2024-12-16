package com.ureca.filmeet.domain.review.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.dto.request.CreateCommentRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateCommentResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.repository.ReviewCommentRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
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
    private ReviewCommentRepository reviewCommentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        reviewCommentRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }


    @DisplayName("댓글 저장 - 댓글_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void commentCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        // given
        User reviewer = createUser("reviewer", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 제목", movie, reviewer);

        userRepository.save(reviewer);
        movieRepository.save(movie);
        reviewRepository.save(review);

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            User user = createUser("user" + i, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임" + i,
                    "https://example.com/profile" + i + ".jpg");
            users.add(user);
        }
        userRepository.saveAll(users);

        // when
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            int commentNum = i + 1;
            CreateCommentRequest createCommentRequest = new CreateCommentRequest(review.getId(), "내용" + commentNum);

            executorService.submit(() -> {
                try {
                    reviewCommentCommandService.createComment(createCommentRequest, userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then
        Optional<Review> findReview = reviewRepository.findById(review.getId());
        assertThat(findReview).isPresent();
        assertThat(findReview.get().getCommentCounts()).isEqualTo(1000);
    }

    @DisplayName("댓글 삭제 - 댓글_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void deleteCommentCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        // given
        User reviewer = createUser("reviewer", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 제목", movie, reviewer);

        userRepository.save(reviewer);
        movieRepository.save(movie);
        reviewRepository.save(review);

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            User user = createUser("user" + i, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임" + i,
                    "https://example.com/profile" + i + ".jpg");
            users.add(user);
        }
        userRepository.saveAll(users);

        // when
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch likeLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch cancelLatch = new CountDownLatch(numberOfThreads);

        List<Long> commentIds = new CopyOnWriteArrayList<>();

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            CreateCommentRequest createCommentRequest = new CreateCommentRequest(review.getId(), "내용");

            executorService.submit(() -> {
                try {
                    CreateCommentResponse comment = reviewCommentCommandService.createComment(createCommentRequest,
                            userId);
                    commentIds.add(comment.reviewCommentId());
                } finally {
                    likeLatch.countDown();
                }
            });
        }
        likeLatch.await();

        for (int i = 0; i < numberOfThreads; i++) {
            long commentId = commentIds.get(i);
            executorService.submit(() -> {
                try {
                    reviewCommentCommandService.deleteComment(review.getId(), commentId);
                } finally {
                    cancelLatch.countDown();
                }
            });
        }
        cancelLatch.await();
        executorService.shutdown();

        // then
        Optional<Review> findReview = reviewRepository.findById(review.getId());
        assertThat(findReview).isPresent();
        assertThat(findReview.get().getCommentCounts()).isEqualTo(0);
    }
}
