package com.ureca.filmeet.domain.review.service.query;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createReview;
import static com.ureca.filmeet.global.util.TestUtils.createReviewComment;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewDetailResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewComment;
import com.ureca.filmeet.domain.review.entity.ReviewLikes;
import com.ureca.filmeet.domain.review.repository.ReviewCommentRepository;
import com.ureca.filmeet.domain.review.repository.ReviewLikesRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("local")
@Transactional
@SpringBootTest
class ReviewQueryServiceTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    @Autowired
    private ReviewLikesRepository reviewLikesRepository;

    @Autowired
    private ReviewQueryService reviewQueryService;

    @Test
    @DisplayName("영화 리뷰 상세 정보를 댓글 및 좋아요 상태와 함께 조회한다.")
    void getMovieReviewDetail_whenReviewExists_returnsReviewDetailResponse() {
        // given
        Movie movie = createMovie("제목1", "줄거리", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        User user = createUser("username", "securePassword", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임",
                "https://example.com/profile.jpg");
        Review review = createReview("리뷰 내용", movie, user);
        ReviewComment comment1 = createReviewComment("리뷰 댓글 내용1", review, user);
        ReviewComment comment2 = createReviewComment("리뷰 댓글 내용2", review, user);

        // When
        movieRepository.save(movie);
        userRepository.save(user);
        reviewRepository.save(review);
        reviewCommentRepository.save(comment1);
        reviewCommentRepository.save(comment2);
        reviewLikesRepository.save(new ReviewLikes(review, user));
        Long reviewId = review.getId();
        Long userId = user.getId();
        GetMovieReviewDetailResponse result = reviewQueryService.getMovieReviewDetail(reviewId, userId);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(
                        GetMovieReviewDetailResponse::reviewId,
                        GetMovieReviewDetailResponse::userId,
                        GetMovieReviewDetailResponse::movieId,
                        GetMovieReviewDetailResponse::content,
                        GetMovieReviewDetailResponse::likeCounts,
                        GetMovieReviewDetailResponse::commentCounts,
                        GetMovieReviewDetailResponse::nickName,
                        GetMovieReviewDetailResponse::profileImage,
                        GetMovieReviewDetailResponse::movieTitle,
                        GetMovieReviewDetailResponse::posterUrl,
                        GetMovieReviewDetailResponse::isLiked
                )
                .containsExactly(reviewId, userId, movie.getId(), review.getContent(), review.getLikeCounts(),
                        review.getCommentCounts(), review.getUser().getNickname(), review.getUser().getProfileImage(),
                        review.getMovie().getTitle(), review.getMovie().getPosterUrl(), true);
    }
}