package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import java.math.BigDecimal;

public record MoviesRatingResponse(

        Long movieId,
        String movieTitle,
        String posterUrl,
        BigDecimal ratingScore
) {

    public static MoviesRatingResponse of(MovieRatings movieRatings) {
        return new MoviesRatingResponse(
                movieRatings.getMovie().getId(),
                movieRatings.getMovie().getTitle(),
                movieRatings.getMovie().getPosterUrl(),
                movieRatings.getRatingScore()
        );
    }
}
