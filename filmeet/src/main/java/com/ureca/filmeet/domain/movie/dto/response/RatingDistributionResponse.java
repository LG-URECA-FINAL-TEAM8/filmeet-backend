package com.ureca.filmeet.domain.movie.dto.response;

import java.math.BigDecimal;

public record RatingDistributionResponse(

        BigDecimal ratingScore,
        Long ratingCount
) {

}