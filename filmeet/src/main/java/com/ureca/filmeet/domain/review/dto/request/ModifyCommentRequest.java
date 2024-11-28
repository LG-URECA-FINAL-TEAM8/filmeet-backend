package com.ureca.filmeet.domain.review.dto.request;

public record ModifyCommentRequest(

        Long reviewCommentId,
        String content
) {
}
