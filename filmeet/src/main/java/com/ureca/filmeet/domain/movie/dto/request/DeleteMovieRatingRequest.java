package com.ureca.filmeet.domain.movie.dto.request;

public record DeleteMovieRatingRequest(

        Long movieRatingId,
        Long movieId,
        Long userId
) {
}
