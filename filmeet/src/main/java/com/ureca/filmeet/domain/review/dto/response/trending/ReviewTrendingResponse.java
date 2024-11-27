package com.ureca.filmeet.domain.review.dto.response.trending;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReviewTrendingResponse(

        Long reviewId,
        Long userId,
        Long movieId,
        String content,
        int likeCounts,
        int commentCounts,
        LocalDateTime reviewCreatedAt,
        String nickname,
        String profileImage,
        String movieTitle,
        String posterUrl,
        BigDecimal ratingScore,
        Boolean isLiked,
        Double popularityScore
) {

    public static ReviewTrendingResponse from(ReviewResponse response, double popularityScore) {
        return new ReviewTrendingResponse(
                response.reviewId(),
                response.userId(),
                response.movieId(),
                response.content(),
                response.likeCounts(),
                response.commentCounts(),
                response.createdAt(),
                response.nickname(),
                response.profileImage(),
                response.movieTitle(),
                response.posterUrl(),
                response.ratingScore(),
                response.isLiked(),
                popularityScore
        );
    }
}
