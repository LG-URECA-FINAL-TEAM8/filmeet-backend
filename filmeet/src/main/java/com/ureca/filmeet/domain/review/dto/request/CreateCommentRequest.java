package com.ureca.filmeet.domain.review.dto.request;

public record CreateCommentRequest(

        Long reviewId,
        Long userId,
        String content
) {
}
