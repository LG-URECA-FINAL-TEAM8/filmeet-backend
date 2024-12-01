package com.ureca.filmeet.domain.review.entity;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
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
class ReviewTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("리뷰를 생성하고 초기 상태를 확인한다.")
    void createReview_whenValidData_createsReview() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Review savedReview = reviewRepository.save(review);

        // then
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getContent()).isEqualTo("리뷰 내용");
        assertThat(savedReview.getLikeCounts()).isEqualTo(0);
        assertThat(savedReview.getCommentCounts()).isEqualTo(0);
        assertThat(savedReview.getIsVisible()).isTrue();
        assertThat(savedReview.getMovie()).isEqualTo(movie);
        assertThat(savedReview.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("리뷰의 댓글 수를 증가시킨다.")
    void addCommentCounts_increasesCommentCount() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Review savedReview = reviewRepository.save(review);
        savedReview.addCommentCounts();
        savedReview.addCommentCounts();
        Optional<Review> findReview = reviewRepository.findById(savedReview.getId());

        // then
        assertThat(findReview)
                .isPresent()
                .get()
                .extracting(Review::getCommentCounts)
                .isEqualTo(2);
    }

    @Test
    @DisplayName("댓글 수가 0인 리뷰에서 댓글 수를 감소시키면 0을 유지한다.")
    void decrementCommentCounts_whenZero_doesNotGoBelowZero() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Review savedReview = reviewRepository.save(review);
        savedReview.decrementCommentCounts();
        Optional<Review> findReview = reviewRepository.findById(savedReview.getId());

        // then
        assertThat(findReview)
                .isPresent()
                .get()
                .extracting(Review::getCommentCounts)
                .isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 수가 0 이상인 리뷰에서 댓글 수를 감소시킨다.")
    void decrementCommentCounts_whenPositive_decreasesCommentCount() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Review savedReview = reviewRepository.save(review);
        savedReview.addCommentCounts();
        savedReview.addCommentCounts();
        savedReview.decrementCommentCounts();
        Optional<Review> findReview = reviewRepository.findById(savedReview.getId());

        // then
        assertThat(findReview)
                .isPresent()
                .get()
                .extracting(Review::getCommentCounts)
                .isEqualTo(1);
    }

    @Test
    @DisplayName("리뷰의 좋아요 수를 증가시킨다.")
    void addLikeCounts_increasesLikeCount() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Review savedReview = reviewRepository.save(review);
        savedReview.addLikeCounts();
        savedReview.addLikeCounts();
        Optional<Review> findReview = reviewRepository.findById(savedReview.getId());

        // then
        assertThat(findReview)
                .isPresent()
                .get()
                .extracting(Review::getLikeCounts)
                .isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요 수가 0인 리뷰에서 좋아요 수를 감소시키면 0을 유지한다.")
    void decrementLikesCounts_whenZero_doesNotGoBelowZero() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Review savedReview = reviewRepository.save(review);
        savedReview.decrementLikesCounts();
        Optional<Review> findReview = reviewRepository.findById(savedReview.getId());

        // then
        assertThat(findReview)
                .isPresent()
                .get()
                .extracting(Review::getLikeCounts)
                .isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요 수가 0 이상인 리뷰에서 좋아요 수를 감소시킨다.")
    void decrementLikesCounts_whenPositive_decreasesLikeCount() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Review savedReview = reviewRepository.save(review);
        savedReview.addLikeCounts();
        savedReview.addLikeCounts();
        savedReview.decrementLikesCounts();
        Optional<Review> findReview = reviewRepository.findById(savedReview.getId());

        // then
        assertThat(findReview)
                .isPresent()
                .get()
                .extracting(Review::getLikeCounts)
                .isEqualTo(1);
    }

    @Test
    @DisplayName("리뷰가 초기 생성 시 isVisible은 true로 isDeleted는 false로 댓글 개수와 좋아요 개수는 0으로 설정된다.")
    void reviewVisibility_initialState_isTrue() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Review savedReview = reviewRepository.save(review);
        Optional<Review> findReview = reviewRepository.findById(savedReview.getId());

        // then
        assertThat(findReview)
                .isPresent()
                .get()
                .extracting(
                        Review::getIsVisible,
                        Review::getIsDeleted,
                        Review::getCommentCounts,
                        Review::getLikeCounts
                )
                .contains(true, false, 0, 0);
    }
}