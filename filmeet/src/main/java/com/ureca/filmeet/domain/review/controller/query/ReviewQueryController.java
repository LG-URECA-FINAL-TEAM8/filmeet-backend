package com.ureca.filmeet.domain.review.controller.query;

import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewDetailResponse;
import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
import com.ureca.filmeet.domain.review.dto.response.ReviewCommentResponse;
import com.ureca.filmeet.domain.review.dto.response.trending.ReviewTrendingResponse;
import com.ureca.filmeet.domain.review.service.query.ReviewQueryService;
import com.ureca.filmeet.domain.review.service.query.ReviewTrendingQueryService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<ApiResponse<SliceResponseDto<GetMovieReviewsResponse>>> getMovieReviews(
            @PathVariable("movieId") Long movieId,
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        Long userId = (user != null) ? user.getId() : null;
        Slice<GetMovieReviewsResponse> movieReviews = reviewQueryService.getMovieReviews(movieId, userId,
                pageable);
        return ApiResponse.ok(SliceResponseDto.of(movieReviews));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<GetMovieReviewDetailResponse>> getMovieReviewDetail(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        Long userId = (user != null) ? user.getId() : null;
        GetMovieReviewDetailResponse movieReviewDetail = reviewQueryService.getMovieReviewDetail(reviewId, userId);
        return ApiResponse.ok(movieReviewDetail);
    }

    @GetMapping("/{reviewId}/comments")
    public ResponseEntity<ApiResponse<SliceResponseDto<ReviewCommentResponse>>> getMovieReviewComments(
            @PathVariable("reviewId") Long reviewId,
            Pageable pageable
    ) {

        Slice<ReviewCommentResponse> reviewComments = reviewQueryService.getMovieReviewComments(reviewId, pageable);
        return ApiResponse.ok(SliceResponseDto.of(reviewComments));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<SliceResponseDto<ReviewTrendingResponse>>> getTrendingReviews(
            @RequestParam("filter") String filter,
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        Long userId = (user != null) ? user.getId() : null;
        if (filter.equals("recent_reviews")) {
            Slice<ReviewTrendingResponse> recentReviews = reviewTrendingQueryService.getRecentReviews(userId,
                    pageable);
            return ApiResponse.ok(SliceResponseDto.of(recentReviews));
        }
        LocalDateTime currentTime = LocalDateTime.now();
        Slice<ReviewTrendingResponse> trendingReviews = reviewTrendingQueryService.getTrendingReviews(userId,
                pageable, currentTime);
        return ApiResponse.ok(SliceResponseDto.of(trendingReviews));
    }
}