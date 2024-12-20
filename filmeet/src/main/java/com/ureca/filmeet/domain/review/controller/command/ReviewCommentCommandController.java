package com.ureca.filmeet.domain.review.controller.command;

import com.ureca.filmeet.domain.review.dto.request.CreateCommentRequest;
import com.ureca.filmeet.domain.review.dto.request.ModifyCommentRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateCommentResponse;
import com.ureca.filmeet.domain.review.dto.response.ModifyCommentResponse;
import com.ureca.filmeet.domain.review.service.command.ReviewCommentCommandService;
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
public class ReviewCommentCommandController {

    private final ReviewCommentCommandService reviewCommentCommandService;

    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CreateCommentResponse>> createComment(
            @RequestBody CreateCommentRequest createCommentRequest,
            @AuthenticationPrincipal User user
    ) {

        CreateCommentResponse reviewComment = reviewCommentCommandService.createComment(createCommentRequest,
                user.getId());
        return ApiResponse.ok(reviewComment);
    }

    @PatchMapping("/comments")
    public ResponseEntity<ApiResponse<ModifyCommentResponse>> modifyComment(
            @RequestBody ModifyCommentRequest modifyCommentRequest
    ) {

        ModifyCommentResponse modifyCommentResponse = reviewCommentCommandService.modifyComment(modifyCommentRequest);
        return ApiResponse.ok(modifyCommentResponse);
    }

    @DeleteMapping("/{reviewId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("commentId") Long commentId
    ) {
        reviewCommentCommandService.deleteComment(reviewId, commentId);
        return ApiResponse.ok("댓글을 삭제 했습니다.");
    }
}
