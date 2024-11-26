package com.ureca.filmeet.domain.review.controller.command;

import com.ureca.filmeet.domain.review.service.command.ReviewLikesCommandService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/reviews/{reviewId}/users/{userId}")
    public void reviewLikes(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewLikesCommandService.reviewLikes(reviewId, userId);
    }

    @DeleteMapping("/cancel/reviews/{reviewId}/users/{userId}")
    public void reviewLikesCancel(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("userId") Long userId
    ) {
        reviewLikesCommandService.reviewLikesCancel(reviewId, userId);
    }
}
