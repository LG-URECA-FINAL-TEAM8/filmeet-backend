package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import java.math.BigDecimal;

public record MyMovieRating(

        Long movieRatingId,
        BigDecimal ratingScore
) {
    public static MyMovieRating of(MovieRatings movieRatings) {
        return new MyMovieRating(
                movieRatings.getId(),
                movieRatings.getRatingScore()
        );
    }
}
