package com.ureca.filmeet.domain.user.controller.query;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRatingResponse;
import com.ureca.filmeet.domain.movie.service.query.MovieRatingQueryService;
import com.ureca.filmeet.domain.review.dto.response.UserReviewsResponse;
import com.ureca.filmeet.domain.review.service.query.ReviewQueryService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.ureca.filmeet.domain.user.service.command.UserCommandService;
import com.ureca.filmeet.domain.user.service.query.UserQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserQueryController {
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final ReviewQueryService reviewQueryService;
    private final MovieRatingQueryService movieRatingQueryService;

    @GetMapping("/info")
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole(),
                "nickname", user.getNickname()
        ));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<SliceResponseDto<UserReviewsResponse>>> getUserReviews(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        Slice<UserReviewsResponse> userReviews = reviewQueryService.getUserReviews(user.getId(), pageable);
        return ApiResponse.ok(SliceResponseDto.of(userReviews));
    }

    @GetMapping("/movies/ratings")
    public ResponseEntity<ApiResponse<SliceResponseDto<MoviesRatingResponse>>> getMoviesWithUserRatings(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        Slice<MoviesRatingResponse> moviesWithUserRatings = movieRatingQueryService.getMoviesWithUserRatings(
                user.getId(),
                pageable);
        return ApiResponse.ok(SliceResponseDto.of(moviesWithUserRatings));
    }  
      
    @GetMapping("/check-username")
    public ResponseEntity<?> checkDuplicateUsername(@RequestParam String username) {
        Boolean isAvailable = userQueryService.existsByUsername(username);
        return ApiResponse.ok(Map.of(
                "isAvailable", isAvailable
        ));
    }
}
