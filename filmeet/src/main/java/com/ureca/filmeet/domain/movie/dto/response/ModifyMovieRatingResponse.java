package com.ureca.filmeet.domain.movie.dto.response;

public record ModifyMovieRatingResponse(

        Long movieRatingsId
) {

    public static ModifyMovieRatingResponse of(Long movieRatingsId) {
        return new ModifyMovieRatingResponse(
                movieRatingsId
        );
    }
}