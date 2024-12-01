package com.ureca.filmeet.domain.review.controller.query;

import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewDetailResponse;
import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
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
        Slice<GetMovieReviewsResponse> movieReviews = reviewQueryService.getMovieReviews(movieId, user.getId(),
                pageable);
        return ApiResponse.ok(SliceResponseDto.of(movieReviews));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<GetMovieReviewDetailResponse>> getMovieReviewDetail(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        GetMovieReviewDetailResponse movieReviewDetail = reviewQueryService.getMovieReviewDetail(reviewId,
                user.getId());
        return ApiResponse.ok(movieReviewDetail);
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<SliceResponseDto<ReviewTrendingResponse>>> getTrendingReviews(
            @RequestParam("filter") String filter,
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        if (filter.equals("recent_reviews")) {
            Slice<ReviewTrendingResponse> recentReviews = reviewTrendingQueryService.getRecentReviews(user.getId(),
                    pageable);
            return ApiResponse.ok(SliceResponseDto.of(recentReviews));
        }
        LocalDateTime currentTime = LocalDateTime.now();
        Slice<ReviewTrendingResponse> trendingReviews = reviewTrendingQueryService.getTrendingReviews(user.getId(),
                pageable, currentTime);
        return ApiResponse.ok(SliceResponseDto.of(trendingReviews));
    }
}