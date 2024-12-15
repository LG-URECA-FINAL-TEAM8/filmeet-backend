package com.ureca.filmeet.domain.review.service.query;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.dto.response.trending.ReviewTrendingResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("local")
@Transactional
@SpringBootTest
class ReviewTrendingQueryServiceTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewTrendingQueryService reviewTrendingQueryService;

    @Test
    @DisplayName("좋아요 수가 많을수록 높은 점수를 받는다.")
    void calculatePopularityScore_moreLikesHigherScore() {
        // given
        LocalDateTime now = LocalDateTime.now();
        String username = UUID.randomUUID() + "";
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);
        User user = createUser(username, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임1",
                "https://profile.url");

        Review lowLikesReview = createReview("좋아요 적음", movie, user, 2, 3);
        Review highLikesReview = createReview("좋아요 많음", movie, user, 3, 3);

        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(lowLikesReview);
        reviewRepository.save(highLikesReview);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ReviewTrendingResponse> result = reviewTrendingQueryService.getTrendingReviews(user.getId(), pageable,
                now);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting(ReviewTrendingResponse::reviewId)
                .containsExactly(highLikesReview.getId(), lowLikesReview.getId());
    }

    @Test
    @DisplayName("댓글 수가 많을수록 높은 점수를 받는다.")
    void calculatePopularityScore_moreCommentsHigherScore() {
        // given
        LocalDateTime now = LocalDateTime.now();
        String username = UUID.randomUUID() + "";
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);
        User user = createUser(username, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임1",
                "https://profile.url");

        Review lowCommentsReview = createReview("좋아요 적음", movie, user, 3, 3);
        Review highCommentsReview = createReview("좋아요 많음", movie, user, 3, 4);

        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(lowCommentsReview);
        reviewRepository.save(highCommentsReview);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<ReviewTrendingResponse> result = reviewTrendingQueryService.getTrendingReviews(user.getId(), pageable,
                now);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting(ReviewTrendingResponse::reviewId)
                .containsExactly(highCommentsReview.getId(), lowCommentsReview.getId());
    }
}