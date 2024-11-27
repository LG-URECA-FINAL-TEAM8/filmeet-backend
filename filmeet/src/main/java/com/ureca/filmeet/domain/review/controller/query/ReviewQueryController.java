package com.ureca.filmeet.domain.review.controller.query;

import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewDetailResponse;
import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
import com.ureca.filmeet.domain.review.dto.response.trending.ReviewTrendingResponse;
import com.ureca.filmeet.domain.review.service.query.ReviewQueryService;
import com.ureca.filmeet.domain.review.service.query.ReviewTrendingQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewQueryController {

    private final ReviewQueryService reviewQueryService;
    private final ReviewTrendingQueryService reviewTrendingQueryService;

    @GetMapping("/movies/{movieId}/users/{userId}")
    public ResponseEntity<ApiResponse<SliceResponseDto<GetMovieReviewsResponse>>> getMovieReviews(
            @PathVariable("movieId") Long movieId,
            @PathVariable("userId") Long userId,
            Pageable pageable
    ) {
        Slice<GetMovieReviewsResponse> movieReviews = reviewQueryService.getMovieReviews(movieId, userId, pageable);
        return ApiResponse.ok(SliceResponseDto.of(movieReviews));
    }

    @GetMapping("/{reviewId}/users/{userId}")
    public ResponseEntity<ApiResponse<GetMovieReviewDetailResponse>> getMovieReviewDetail(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        GetMovieReviewDetailResponse movieReviewDetail = reviewQueryService.getMovieReviewDetail(reviewId, userId);
        return ApiResponse.ok(movieReviewDetail);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<SliceResponseDto<ReviewTrendingResponse>>> getTrendingReviews(
            @RequestParam("filter") String filter,
            @PathVariable("userId") Long userId,
            Pageable pageable
    ) {
        if (filter.equals("recent_reviews")) {
            Slice<ReviewTrendingResponse> recentReviews = reviewTrendingQueryService.getRecentReviews(userId, pageable);
            return ApiResponse.ok(SliceResponseDto.of(recentReviews));
        }
        Slice<ReviewTrendingResponse> trendingReviews = reviewTrendingQueryService.getTrendingReviews(userId, pageable);
        return ApiResponse.ok(SliceResponseDto.of(trendingReviews));
    }
}