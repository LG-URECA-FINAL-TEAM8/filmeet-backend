package com.ureca.filmeet.domain.review.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createReviewComment;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.dto.request.CreateCommentRequest;
import com.ureca.filmeet.domain.review.dto.request.ModifyCommentRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateCommentResponse;
import com.ureca.filmeet.domain.review.dto.response.ModifyCommentResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewComment;
import com.ureca.filmeet.domain.review.exception.ReviewCommentNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewUserNotFoundException;
import com.ureca.filmeet.domain.review.repository.ReviewCommentRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
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

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class ReviewCommentCommandServiceTest {

    @Autowired
    private ReviewCommentCommandService reviewCommentCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;
    @Autowired
    private MovieRepository movieRepository;

    @Test
    @DisplayName("리뷰에 댓글을 성공적으로 저장한다.")
    void createComment_whenValidRequest_savesComment() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        CreateCommentRequest request = new CreateCommentRequest(review.getId(), "댓글 내용");
        CreateCommentResponse response = reviewCommentCommandService.createComment(request, user.getId());
        Optional<ReviewComment> savedComment = reviewCommentRepository.findById(response.reviewCommentId());

        // then
        assertThat(savedComment)
                .isPresent()
                .get()
                .extracting("id", "content", "user", "review")
                .contains(
                        savedComment.get().getId(), "댓글 내용", user, review
                );
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 ID로 댓글을 생성하면 ReviewNotFoundException 예외가 발생한다.")
    void createComment_whenReviewNotFound_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");

        // when
        userRepository.save(user);
        CreateCommentRequest request = new CreateCommentRequest(999L, "댓글 내용");

        // then
        assertThatThrownBy(() -> reviewCommentCommandService.createComment(request, user.getId()))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 댓글을 생성하면 ReviewUserNotFoundException 예외가 발생한다.")
    void createComment_whenUserNotFound_throwsException() {
        // given
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, null);

        // when
        movieRepository.save(movie);
        reviewRepository.save(review);
        CreateCommentRequest request = new CreateCommentRequest(review.getId(), "댓글 내용");

        // then
        assertThatThrownBy(() -> reviewCommentCommandService.createComment(request, 999L))
                .isInstanceOf(ReviewUserNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰에 달려 있는 댓글을 성공적으로 수정한다.")
    void modifyComment_whenValidRequest_updatesComment() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);
        ReviewComment reviewComment = createReviewComment("댓글 내용", review, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        reviewCommentRepository.save(reviewComment);
        ModifyCommentRequest request = new ModifyCommentRequest(reviewComment.getId(), "수정된 댓글 내용");
        ModifyCommentResponse response = reviewCommentCommandService.modifyComment(request);
        Optional<ReviewComment> savedComment = reviewCommentRepository.findById(response.reviewCommentId());

        // then
        assertThat(savedComment)
                .isPresent()
                .get()
                .extracting("id", "content", "user", "review")
                .contains(
                        savedComment.get().getId(), "수정된 댓글 내용", user, review
                );
    }

    @Test
    @DisplayName("존재하지 않는 댓글을 수정하려 하면 ReviewCommentNotFoundException 예외가 발생한다.")
    void modifyComment_whenCommentNotFound_throwsException() {
        // given
        ModifyCommentRequest request = new ModifyCommentRequest(999L, "수정된 댓글 내용");

        // when & then
        assertThatThrownBy(() -> reviewCommentCommandService.modifyComment(request))
                .isInstanceOf(ReviewCommentNotFoundException.class);
    }

    @Test
    @DisplayName("댓글을 성공적으로 삭제한다.")
    void deleteComment_whenValidRequest_deletesComment() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user, 0, 1);
        ReviewComment reviewComment = createReviewComment("댓글 내용", review, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        reviewCommentRepository.save(reviewComment);
        reviewCommentCommandService.deleteComment(review.getId(), reviewComment.getId());
        Optional<ReviewComment> deletedComment = reviewCommentRepository.findById(reviewComment.getId());

        // then
        assertThat(deletedComment).isPresent();
        assertThat(deletedComment.get().getIsDeleted()).isTrue();
        assertThat(review.getCommentCounts()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 ID 또는 댓글 ID로 삭제를 시도하면 ReviewCommentNotFoundException 예외가 발생한다.")
    void deleteComment_whenCommentNotFound_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);
        ReviewComment reviewComment = createReviewComment("댓글 내용", review, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        reviewCommentRepository.save(reviewComment);

        // then
        assertThatThrownBy(() -> reviewCommentCommandService.deleteComment(review.getId(), 999L))
                .isInstanceOf(ReviewCommentNotFoundException.class);
        assertThatThrownBy(() -> reviewCommentCommandService.deleteComment(999L, reviewComment.getId()))
                .isInstanceOf(ReviewCommentNotFoundException.class);
    }
}