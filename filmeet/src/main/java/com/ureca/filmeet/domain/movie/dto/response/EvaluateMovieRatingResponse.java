package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.entity.MovieRatings;

public record EvaluateMovieRatingResponse(

        Long movieRatingId
) {

    public static EvaluateMovieRatingResponse of(MovieRatings movieRatings) {
        return new EvaluateMovieRatingResponse(
                movieRatings.getId()
        );
    }
}
