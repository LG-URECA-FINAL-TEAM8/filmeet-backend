package com.ureca.filmeet.domain.review.dto.request;

public record CreateReviewRequest(

        Long movieId,
        Long userId,
        String content
) {
}
