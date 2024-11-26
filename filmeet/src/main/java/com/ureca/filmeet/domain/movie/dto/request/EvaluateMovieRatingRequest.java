package com.ureca.filmeet.domain.movie.dto.request;

import java.math.BigDecimal;

public record EvaluateMovieRatingRequest(

        Long movieId,
        Long userId,
        BigDecimal ratingScore
) {
}
