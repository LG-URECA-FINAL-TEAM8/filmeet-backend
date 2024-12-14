package com.ureca.filmeet.domain.movie.dto.response;

import java.math.BigDecimal;

public record UserMovieInteractionResponse(
        Long movieRatingId,
        BigDecimal ratingScore,
        Long reviewId,
        String content,
        String userProfileImage,
        Boolean isLiked
) {
}