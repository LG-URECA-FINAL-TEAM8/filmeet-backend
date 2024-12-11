package com.ureca.filmeet.domain.movie.dto.request;

import java.math.BigDecimal;

public record EvaluateMovieRatingRequest(

        Long movieId,
        BigDecimal ratingScore
) {
}
