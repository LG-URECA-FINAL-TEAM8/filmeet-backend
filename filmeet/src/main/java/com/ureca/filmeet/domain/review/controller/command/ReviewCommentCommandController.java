package com.ureca.filmeet.domain.review.controller.command;

import com.ureca.filmeet.domain.review.dto.request.CreateCommentRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateCommentResponse;
import com.ureca.filmeet.domain.review.service.command.ReviewCommentCommandService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewCommentCommandController {

    private final ReviewCommentCommandService reviewCommentCommandService;

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CreateCommentResponse>> createComment(
            @RequestBody CreateCommentRequest createCommentRequest) {
        CreateCommentResponse reviewComment = reviewCommentCommandService.createComment(createCommentRequest);
        return ApiResponse.ok(CreateCommentResponse.of(reviewComment.reviewCommentId()));
    }
}
