package com.ureca.filmeet.domain.review.repository;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createReviewLikes;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewLikes;
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
class ReviewLikesRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikesRepository reviewLikesRepository;

    @DisplayName("사용자는 리뷰에 좋아요를 성공적으로 저장한다.")
    @Test
    void saveReviewLikesSuccessfully() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        ReviewLikes reviewLikes = createReviewLikes(review, user);

        // when
        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(review);
        ReviewLikes savedReviewLikes = reviewLikesRepository.save(reviewLikes);

        // then
        assertThat(savedReviewLikes).isNotNull();
        assertThat(savedReviewLikes.getId()).isNotNull();
        assertThat(savedReviewLikes.getReview()).isEqualTo(review);
        assertThat(savedReviewLikes.getUser()).isEqualTo(user);
    }

    @DisplayName("사용자는 리뷰에 저장된 좋아요를 삭제할 수 있다.")
    @Test
    void deleteReviewLikesSuccessfully() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        ReviewLikes reviewLikes = createReviewLikes(review, user);

        // when
        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(review);
        ReviewLikes savedReviewLikes = reviewLikesRepository.save(reviewLikes);
        reviewLikesRepository.delete(savedReviewLikes);
        Optional<ReviewLikes> deletedReviewLikes = reviewLikesRepository.findById(savedReviewLikes.getId());

        // then
        assertThat(deletedReviewLikes).isNotPresent();
    }

    @DisplayName("리뷰와 사용자가 존재하지 않는 경우 Optional.empty를 반환한다.")
    @Test
    void findReviewLikesByReviewIdAndUserId_whenNotExists_returnsOptionalEmpty() {
        // when
        Optional<ReviewLikes> result = reviewLikesRepository.findReviewLikesByReviewIdAndUserId(999L, 999L);

        // then
        assertThat(result).isNotPresent();
    }

    @DisplayName("리뷰와 사용자가 존재하는 경우 true를 반환한다.")
    @Test
    void existsByReviewIdAndUserId_whenExists_returnsTrue() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        ReviewLikes reviewLikes = createReviewLikes(review, user);

        // when
        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(review);
        reviewLikesRepository.save(reviewLikes);
        boolean exists = reviewLikesRepository.existsByReviewIdAndUserId(review.getId(), user.getId());

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("리뷰와 사용자가 존재하지 않는 경우 false를 반환한다.")
    @Test
    void existsByReviewIdAndUserId_whenNotExists_returnsFalse() {
        // when
        boolean exists = reviewLikesRepository.existsByReviewIdAndUserId(999L, 999L);

        // then
        assertThat(exists).isFalse();
    }
}