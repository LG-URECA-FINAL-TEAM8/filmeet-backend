package com.ureca.filmeet.domain.review.dto.response;

public record CreateReviewResponse(

        Long reviewId
) {

    public static CreateReviewResponse of(Long reviewId) {
        return new CreateReviewResponse(
                reviewId
        );
    }
}
