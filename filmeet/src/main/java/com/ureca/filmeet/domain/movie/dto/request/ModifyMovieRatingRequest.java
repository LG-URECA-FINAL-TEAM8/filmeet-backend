package com.ureca.filmeet.domain.movie.dto.request;

import java.math.BigDecimal;

public record ModifyMovieRatingRequest(

        Long movieId,
        Long movieRatingId,
        BigDecimal oldRatingScore,
        BigDecimal newRatingScore
) {
}
