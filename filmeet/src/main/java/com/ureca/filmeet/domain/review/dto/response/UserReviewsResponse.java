package com.ureca.filmeet.domain.review.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserReviewsResponse(

        Long reviewId,
        Long userId,
        Long movieId,
        String reviewContent,
        int likeCounts,
        int commentCounts,
        String nickname,
        String profileImage,
        String movieTitle,
        String posterUrl,
        LocalDate releaseDate,
        BigDecimal ratingScore,
        Boolean isLiked
) {
}
