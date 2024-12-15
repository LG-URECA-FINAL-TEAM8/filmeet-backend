package com.ureca.filmeet.domain.review.repository;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createReviewComment;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewComment;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("local")
@Transactional
@SpringBootTest
class ReviewCommentRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    @DisplayName("리뷰와 리뷰에 달려 있는 댓글을 성공적으로 조회한다.")
    @Test
    void findReviewAndReviewCommentById_returnsCorrectReviewAndComment() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        ReviewComment reviewComment = createReviewComment("리뷰 댓글 내용", review, user);

        // when
        Movie savedMovie = movieRepository.save(movie);
        User savedUser = userRepository.save(user);
        Review savedReview = reviewRepository.save(review);
        ReviewComment savedReviewComment = reviewCommentRepository.save(reviewComment);
        Optional<ReviewComment> reviewWithComment = reviewCommentRepository.findReviewCommentBy(savedReview.getId(),
                savedReviewComment.getId());
        Review findReview = reviewComment.getReview();

        // then
        assertThat(reviewWithComment)
                .isPresent()
                .get()
                .extracting("content", "review", "user")
                .contains(
                        "리뷰 댓글 내용", savedReview, savedUser
                );

        assertThat(findReview)
                .isNotNull()
                .extracting("content", "user", "movie")
                .contains(
                        "리뷰 내용", savedUser, savedMovie
                );
    }

    @DisplayName("존재하지 않는 리뷰 ID 또는 댓글 ID로 조회 시 빈 값을 반환한다.")
    @Test
    void findReviewAndReviewCommentById_whenIdsDoNotExist_returnsEmpty() {
        // when
        Optional<ReviewComment> result = reviewCommentRepository.findReviewCommentBy(999L, 999L);

        // then
        assertThat(result).isNotPresent();
    }

    @DisplayName("삭제된 리뷰 댓글은 조회되지 않는다.")
    @Test
    void findReviewAndReviewCommentById_whenCommentIsDeleted_returnsEmpty() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        ReviewComment reviewComment = createReviewComment("삭제된 댓글 내용", review, user);

        // when
        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(review);
        reviewCommentRepository.save(reviewComment);
        reviewComment.delete();
        Optional<ReviewComment> result = reviewCommentRepository.findReviewCommentBy(review.getId(),
                reviewComment.getId());

        // then
        assertThat(result).isNotPresent();
    }

    @DisplayName("댓글 ID, 리뷰 ID 둘중 하나가 일치하지 않으면 조회되지 않는다.")
    @Test
    void findReviewAndReviewCommentById_whenReviewIdDoesNotMatch_returnsEmpty() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        Review anotherReview = createReview("다른 리뷰 내용", movie, user);
        ReviewComment reviewComment = createReviewComment("리뷰 댓글 내용", review, user);
        ReviewComment anotherReviewComment = createReviewComment("리뷰 댓글 내용", anotherReview, user);

        // when
        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(review);
        reviewRepository.save(anotherReview);
        reviewCommentRepository.save(reviewComment);
        Optional<ReviewComment> result1 = reviewCommentRepository.findReviewCommentBy(anotherReview.getId(),
                reviewComment.getId());
        Optional<ReviewComment> result2 = reviewCommentRepository.findReviewCommentBy(review.getId(),
                anotherReviewComment.getId());

        // then
        assertThat(result1).isNotPresent();
        assertThat(result2).isNotPresent();
    }

    @DisplayName("리뷰에 여러 댓글이 있는 경우 특정 댓글만 조회된다.")
    @Test
    void findReviewAndReviewCommentById_whenMultipleCommentsExist_returnsSpecificComment() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        ReviewComment firstComment = createReviewComment("첫 번째 댓글", review, user);
        ReviewComment secondComment = createReviewComment("두 번째 댓글", review, user);

        // when
        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(review);
        reviewCommentRepository.save(firstComment);
        reviewCommentRepository.save(secondComment);
        Optional<ReviewComment> result = reviewCommentRepository.findReviewCommentBy(review.getId(),
                secondComment.getId());

        // then
        assertThat(result)
                .isPresent()
                .get()
                .extracting("content")
                .isEqualTo("두 번째 댓글");
    }

    @DisplayName("리뷰와 댓글이 모두 삭제되었을 때 조회되지 않는다.")
    @Test
    void findReviewAndReviewCommentById_whenReviewAndCommentAreDeleted_returnsEmpty() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        ReviewComment reviewComment = createReviewComment("리뷰 댓글 내용", review, user);

        // when
        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(review);
        reviewCommentRepository.save(reviewComment);
        review.delete(); // 리뷰 삭제
        reviewComment.delete(); // 댓글 삭제
        Optional<ReviewComment> result = reviewCommentRepository.findReviewCommentBy(review.getId(),
                reviewComment.getId());

        // then
        assertThat(result).isNotPresent();
    }
}