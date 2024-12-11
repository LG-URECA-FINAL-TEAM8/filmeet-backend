package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.repository.querydsl.SliceWithCount;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;

public record MoviesRatingWithRatingCountResponse(

        long ratedMovieCount,
        SliceResponseDto<MoviesResponse> moviesResponseSliceResponse
) {

    public static MoviesRatingWithRatingCountResponse of(SliceWithCount<MoviesResponse> moviesResponses) {
        return new MoviesRatingWithRatingCountResponse(
                moviesResponses.getRatedMovieCount(),
                SliceResponseDto.of(moviesResponses)
        );
    }
}
