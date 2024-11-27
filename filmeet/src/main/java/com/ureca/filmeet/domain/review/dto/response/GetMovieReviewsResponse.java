package com.ureca.filmeet.domain.review.dto.response;

public record GetMovieReviewsResponse(

        Long reviewId,
        Long userId,
        String content,
        int likeCounts,
        int commentCounts,
        String nickName,
        String profileImage,
        Boolean isLiked
) {
}