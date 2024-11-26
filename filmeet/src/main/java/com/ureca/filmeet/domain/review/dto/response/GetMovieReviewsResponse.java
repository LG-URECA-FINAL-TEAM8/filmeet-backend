package com.ureca.filmeet.domain.review.dto.response;

import java.math.BigDecimal;

public record GetMovieReviewsResponse(

        Long reviewId,
        Long userId,
        String content,
        int likeCounts,
        int commentCounts,
        BigDecimal star,
        String nickName,
        String profileImage,
        Boolean isLiked
) {
}