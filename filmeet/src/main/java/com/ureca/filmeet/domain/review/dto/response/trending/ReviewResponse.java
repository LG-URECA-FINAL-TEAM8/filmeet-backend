package com.ureca.filmeet.domain.review.dto.response.trending;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewResponse(

        Long reviewId,
        Long userId,
        Long movieId,
        String content,
        int likeCounts,
        int commentCounts,
        LocalDateTime createdAt,
        String nickname,
        String profileImage,
        String movieTitle,
        String posterUrl,
        BigDecimal ratingScore,
        Boolean isLiked
) {
}
