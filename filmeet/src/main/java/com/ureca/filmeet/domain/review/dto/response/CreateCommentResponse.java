package com.ureca.filmeet.domain.review.dto.response;

public record CreateCommentResponse(

        Long reviewCommentId
) {

    public static CreateCommentResponse of(Long reviewCommentId) {
        return new CreateCommentResponse(
                reviewCommentId
        );
    }
}
