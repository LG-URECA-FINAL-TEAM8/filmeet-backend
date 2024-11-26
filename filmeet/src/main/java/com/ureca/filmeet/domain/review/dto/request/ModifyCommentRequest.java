package com.ureca.filmeet.domain.review.dto.request;

public record ModifyCommentRequest(

        Long reviewId,
        String content
) {
}
