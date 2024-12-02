package com.ureca.filmeet.domain.review.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewLikes;
import com.ureca.filmeet.domain.review.exception.ReviewLikeAlreadyExistsException;
import com.ureca.filmeet.domain.review.exception.ReviewLikeNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewUserNotFoundException;
import com.ureca.filmeet.domain.review.repository.ReviewLikesRepository;
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
class ReviewLikesCommandServiceTest {

    @Autowired
    private ReviewLikesCommandService reviewLikesCommandService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikesRepository reviewLikesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    @DisplayName("리뷰에 좋아요를 성공적으로 추가한다.")
    void reviewLikes_whenValidRequest_addsLike() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        reviewLikesCommandService.reviewLikes(review.getId(), user.getId());
        Optional<ReviewLikes> reviewLikes = reviewLikesRepository.findReviewLikesByReviewIdAndUserId(review.getId(),
                user.getId());

        // then
        assertThat(reviewLikes)
                .isPresent()
                .get()
                .extracting("id", "user", "review", "review.likeCounts")
                .contains(
                        reviewLikes.get().getId(), user, review, 1
                );
    }

    @Test
    @DisplayName("이미 좋아요한 리뷰에 다시 좋아요를 누를 경우 ReviewLikeAlreadyExistsException 예외가 발생한다.")
    void reviewLikes_whenAlreadyLiked_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        reviewLikesCommandService.reviewLikes(review.getId(), user.getId());

        // then
        assertThatThrownBy(() -> reviewLikesCommandService.reviewLikes(review.getId(), user.getId()))
                .isInstanceOf(ReviewLikeAlreadyExistsException.class);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 ID로 좋아요를 시도하면 ReviewNotFoundException 예외가 발생한다.")
    void reviewLikes_whenReviewNotFound_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        userRepository.save(user);

        // then
        assertThatThrownBy(() -> reviewLikesCommandService.reviewLikes(999L, user.getId()))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 좋아요를 시도하면 ReviewUserNotFoundException 예외가 발생한다.")
    void reviewLikes_whenUserNotFound_throwsException() {
        // given
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, null);

        // when
        movieRepository.save(movie);
        reviewRepository.save(review);

        // then
        assertThatThrownBy(() -> reviewLikesCommandService.reviewLikes(review.getId(), 999L))
                .isInstanceOf(ReviewUserNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰에 좋아요 취소를 성공적으로 수행한다.")
    void reviewLikesCancel_whenValidRequest_cancelsLike() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        reviewLikesCommandService.reviewLikes(review.getId(), user.getId());
        reviewLikesCommandService.reviewLikesCancel(review.getId(), user.getId());
        Optional<ReviewLikes> reviewLikes = reviewLikesRepository.findReviewLikesByReviewIdAndUserId(review.getId(),
                user.getId());

        // then
        assertThat(reviewLikes).isNotPresent();
        assertThat(review.getLikeCounts()).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 ID로 좋아요 취소를 시도하면 ReviewLikeNotFoundException 예외가 발생한다.")
    void reviewLikesCancel_whenLikeNotFound_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");

        // when
        userRepository.save(user);

        // then
        assertThatThrownBy(() -> reviewLikesCommandService.reviewLikesCancel(999L, user.getId()))
                .isInstanceOf(ReviewLikeNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 좋아요 취소를 시도하면 ReviewLikeNotFoundException 예외가 발생한다.")
    void reviewLikesCancel_whenUserNotFound_throwsException() {
        // given
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, null);

        // when
        movieRepository.save(movie);
        reviewRepository.save(review);

        // then
        assertThatThrownBy(() -> reviewLikesCommandService.reviewLikesCancel(review.getId(), 999L))
                .isInstanceOf(ReviewLikeNotFoundException.class);
    }
}