package com.ureca.filmeet.domain.review.dto.request;

public record ModifyReviewRequest(

        Long reviewId,
        String content
) {
}
