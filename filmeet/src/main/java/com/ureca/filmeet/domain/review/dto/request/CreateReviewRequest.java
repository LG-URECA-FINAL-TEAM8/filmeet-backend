package com.ureca.filmeet.domain.review.dto.request;

public record CreateReviewRequest(

        Long movieId,
        String content
) {
}
