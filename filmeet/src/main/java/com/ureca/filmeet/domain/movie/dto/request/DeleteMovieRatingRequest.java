package com.ureca.filmeet.domain.movie.dto.request;

public record DeleteMovieRatingRequest(

        Long ratingsId,
        Long movieId
) {
}
