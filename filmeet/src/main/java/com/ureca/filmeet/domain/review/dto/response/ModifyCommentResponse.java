package com.ureca.filmeet.domain.review.dto.response;

public record ModifyCommentResponse(

        Long reviewCommentId
) {
    public static ModifyCommentResponse of(Long reviewCommentId) {
        return new ModifyCommentResponse(
                reviewCommentId
        );
    }
}
