package com.ureca.filmeet.domain.movie.dto.request;

import java.math.BigDecimal;

public record ModifyMovieRatingRequest(

        Long movieId,
        Long userId,
        BigDecimal newRatingScore
) {
}
