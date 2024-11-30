package com.ureca.filmeet.domain.movie.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class MovieRatingNotFoundException extends MovieException {

    public MovieRatingNotFoundException() {
        super(ResponseCode.MOVIE_RATING_NOT_FOUND);
    }
}
