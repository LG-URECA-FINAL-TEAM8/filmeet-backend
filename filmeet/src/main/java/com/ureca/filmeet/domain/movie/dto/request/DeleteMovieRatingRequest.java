package com.ureca.filmeet.domain.movie.dto.request;

public record DeleteMovieRatingRequest(

        Long movieId,
        Long userId
) {
}
