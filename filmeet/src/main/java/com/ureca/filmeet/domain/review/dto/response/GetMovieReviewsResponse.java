package com.ureca.filmeet.domain.review.dto.response;

import java.math.BigDecimal;

public record GetMovieReviewsResponse(

        Long reviewId,
        String content,
        int likeCounts,
        int commentCounts,
        BigDecimal star,
        String nickName,
        String profileImage,
        Boolean isLiked // 좋아요 여부 추가

) {
}