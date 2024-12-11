package com.ureca.filmeet.domain.review.controller.command;

import com.ureca.filmeet.domain.review.dto.request.CreateReviewRequest;
import com.ureca.filmeet.domain.review.dto.request.ModifyReviewRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateReviewResponse;
import com.ureca.filmeet.domain.review.dto.response.ModifyReviewResponse;
import com.ureca.filmeet.domain.review.service.command.ReviewCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewCommandController {

    private final ReviewCommandService reviewCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateReviewResponse>> createReview(
            @RequestBody CreateReviewRequest createReviewRequest,
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(reviewCommandService.createReview(createReviewRequest, user.getId()));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<ModifyReviewResponse>> modifyReview(
            @RequestBody ModifyReviewRequest modifyReviewRequest) {
        return ApiResponse.ok(reviewCommandService.modifyReview(modifyReviewRequest));
    }

    @DeleteMapping("/{reviewId}/movies/{movieId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("movieId") Long movieId
    ) {
        reviewCommandService.deleteReview(reviewId, movieId);
        return ApiResponse.ok("리뷰를 삭제 했습니다.");
    }
}
