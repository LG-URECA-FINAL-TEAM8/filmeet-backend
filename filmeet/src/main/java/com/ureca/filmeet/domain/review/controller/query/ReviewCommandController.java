package com.ureca.filmeet.domain.review.controller.query;

import com.ureca.filmeet.domain.review.dto.request.CreateReviewRequest;
import com.ureca.filmeet.domain.review.dto.request.ModifyReviewRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateReviewResponse;
import com.ureca.filmeet.domain.review.dto.response.ModifyReviewResponse;
import com.ureca.filmeet.domain.review.service.query.ReviewCommandService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
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
            @RequestBody CreateReviewRequest createReviewRequest) {
        return ApiResponse.ok(reviewCommandService.createReview(createReviewRequest));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<ModifyReviewResponse>> modifyReview(
            @RequestBody ModifyReviewRequest modifyReviewRequest) {
        return ApiResponse.ok(reviewCommandService.modifyReview(modifyReviewRequest));
    }
}
