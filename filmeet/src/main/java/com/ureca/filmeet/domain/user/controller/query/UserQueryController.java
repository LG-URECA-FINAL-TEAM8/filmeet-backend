package com.ureca.filmeet.domain.user.controller.query;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRatingResponse;
import com.ureca.filmeet.domain.movie.service.query.MovieRatingQueryService;
import com.ureca.filmeet.domain.review.dto.response.UserReviewsResponse;
import com.ureca.filmeet.domain.review.service.query.ReviewQueryService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserQueryController {

    private final ReviewQueryService reviewQueryService;
    private final MovieRatingQueryService movieRatingQueryService;

    @GetMapping("/test")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }

    @GetMapping("/{userId}/reviews")
    public ResponseEntity<ApiResponse<SliceResponseDto<UserReviewsResponse>>> getUserReviews(
            @PathVariable("userId") Long userId,
            Pageable pageable
    ) {
        Slice<UserReviewsResponse> userReviews = reviewQueryService.getUserReviews(userId, pageable);
        return ApiResponse.ok(SliceResponseDto.of(userReviews));
    }

    @GetMapping("{userId}/movies/ratings")
    public ResponseEntity<ApiResponse<SliceResponseDto<MoviesRatingResponse>>> getMoviesWithUserRatings(
            @PathVariable("userId") Long userId,
            Pageable pageable
    ) {
        Slice<MoviesRatingResponse> moviesWithUserRatings = movieRatingQueryService.getMoviesWithUserRatings(userId,
                pageable);
        return ApiResponse.ok(SliceResponseDto.of(moviesWithUserRatings));
    }
}
