package com.ureca.filmeet.domain.review.controller.command;

import com.ureca.filmeet.domain.review.service.command.ReviewLikesCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class ReviewLikesCommandController {

    private final ReviewLikesCommandService reviewLikesCommandService;

    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<String>> reviewLikes(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        reviewLikesCommandService.reviewLikes(reviewId, user.getId());
        return ApiResponse.ok("좋아요를 눌렀습니다.");
    }

    @DeleteMapping("/cancel/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<String>> reviewLikesCancel(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        reviewLikesCommandService.reviewLikesCancel(reviewId, user.getId());
        return ApiResponse.ok("좋아요를 취소 했습니다.");
    }
}
