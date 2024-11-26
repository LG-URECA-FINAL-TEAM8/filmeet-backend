package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import java.math.BigDecimal;

public record MyMovieRating(

        BigDecimal starRating
) {
    public static MyMovieRating of(MovieRatings movieRatings) {
        return new MyMovieRating(
                movieRatings.getStarRating()
        );
    }
}
