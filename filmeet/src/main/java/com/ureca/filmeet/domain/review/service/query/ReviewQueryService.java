package com.ureca.filmeet.domain.review.service.query;

import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewDetailResponse;
import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
import com.ureca.filmeet.domain.review.dto.response.ReviewCommentResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.repository.ReviewLikesRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikesRepository reviewLikesRepository;

    public Slice<GetMovieReviewsResponse> getMovieReviews(Long movieId, Long userId, Pageable pageable) {

        return reviewRepository.findMovieReviewsWithLikes(movieId, userId, pageable);
    }

    public GetMovieReviewDetailResponse getMovieReviewDetail(Long reviewId, Long userId) {
        Review review = reviewRepository.findMovieReviewDetailBy(reviewId)
                .orElseThrow(() -> new RuntimeException("no review"));

        List<ReviewCommentResponse> reviewCommentResponse = review.getReviewComments()
                .stream()
                .map(ReviewCommentResponse::of)
                .toList();

        boolean existsReviewLikes = reviewLikesRepository.existsByReviewIdAndUserId(reviewId, userId);

        return GetMovieReviewDetailResponse.from(review, reviewCommentResponse, existsReviewLikes);
    }
}
