package com.ureca.filmeet.domain.review.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.dto.request.CreateReviewRequest;
import com.ureca.filmeet.domain.review.dto.request.ModifyReviewRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateReviewResponse;
import com.ureca.filmeet.domain.review.dto.response.ModifyReviewResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.exception.ReviewAlreadyExistsException;
import com.ureca.filmeet.domain.review.exception.ReviewMovieNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewUserNotFoundException;
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
class ReviewCommandServiceTest {

    @Autowired
    private ReviewCommandService reviewCommandService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    @DisplayName("리뷰를 성공적으로 저장한다")
    void createReview_whenValidRequest_savesReview() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        CreateReviewRequest request = new CreateReviewRequest(movie.getId(), "리뷰 내용");
        CreateReviewResponse response = reviewCommandService.createReview(request, user.getId());
        Optional<Review> review = reviewRepository.findById(response.reviewId());

        // then
        assertThat(response).isNotNull();
        assertThat(review)
                .isPresent()
                .get()
                .extracting("id", "content", "user", "movie", "likeCounts", "commentCounts")
                .contains(
                        response.reviewId(), "리뷰 내용", user, movie, 0, 0
                );
    }

    @Test
    @DisplayName("영화에 리뷰를 중복으로 생성하면 ReviewAlreadyExistsException 예외가 발생한다.")
    void createReview_whenDuplicateReview_throwsException() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        CreateReviewRequest request = new CreateReviewRequest(movie.getId(), "새로운 리뷰 내용");

        // then
        assertThatThrownBy(() -> reviewCommandService.createReview(request, user.getId()))
                .isInstanceOf(ReviewAlreadyExistsException.class);
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 리뷰를 작성할 경우 ReviewUserNotFoundException 예외가 발생한다.")
    void createReview_whenUserNotFound_throwsException() {
        // given
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);

        // when
        movieRepository.save(movie);
        CreateReviewRequest request = new CreateReviewRequest(movie.getId(), "리뷰 내용");

        // then
        assertThatThrownBy(() -> reviewCommandService.createReview(request, 999L))
                .isInstanceOf(ReviewUserNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 영화 ID에 리뷰를 작성할 경우 ReviewMovieNotFoundException 예외가 발생한다.")
    void createReview_whenMovieNotFound_throwsException() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");

        // when
        userRepository.save(user);
        CreateReviewRequest request = new CreateReviewRequest(999L, "리뷰 내용");

        // then
        assertThatThrownBy(() -> reviewCommandService.createReview(request, user.getId()))
                .isInstanceOf(ReviewMovieNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰를 성공적으로 수정한다.")
    void modifyReview_whenValidRequest_updatesReview() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        ModifyReviewRequest request = new ModifyReviewRequest(review.getId(), "수정된 리뷰 내용");
        ModifyReviewResponse response = reviewCommandService.modifyReview(request);
        Optional<Review> savedReview = reviewRepository.findById(response.reviewId());

        // then
        assertThat(response).isNotNull();
        assertThat(savedReview)
                .isPresent()
                .get()
                .extracting("id", "content", "user", "movie", "likeCounts", "commentCounts")
                .contains(
                        response.reviewId(), "수정된 리뷰 내용", user, movie, 0, 0
                );
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 ID로 리뷰를 수정할 경우 ReviewNotFoundException 예외가 발생한다.")
    void modifyReview_whenReviewNotFound_throwsException() {
        // given
        ModifyReviewRequest request = new ModifyReviewRequest(999L, "수정된 리뷰 내용");

        // when & then
        assertThatThrownBy(() -> reviewCommandService.modifyReview(request))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("리뷰를 성공적으로 삭제한다.")
    void deleteReview_whenValidRequest_updatesVisibility() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Review review = createReview("리뷰 내용", movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);
        reviewCommandService.deleteReview(review.getId(), movie.getId());
        Optional<Review> findReview = reviewRepository.findById(review.getId());

        // then
        assertThat(findReview).isPresent();
        assertThat(findReview.get().getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 ID를 삭제하면 ReviewNotFoundException 가 발생한다. ")
    void deleteReview_whenReviewNotFound_throwsException() {
        // given
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);

        // when
        userRepository.save(user);
        movieRepository.save(movie);

        // then
        assertThatThrownBy(() -> reviewCommandService.deleteReview(999L, movie.getId()))
                .isInstanceOf(ReviewNotFoundException.class);
    }
}