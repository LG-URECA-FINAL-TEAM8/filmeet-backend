package com.ureca.filmeet.domain.review.dto.response;

public record ModifyReviewResponse(

        Long reviewId
) {

    public static ModifyReviewResponse of(Long reviewId) {
        return new ModifyReviewResponse(
                reviewId
        );
    }
}
