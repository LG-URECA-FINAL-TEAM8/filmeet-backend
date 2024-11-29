package com.ureca.filmeet.domain.movie.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class MovieRatingAlreadyExistsException extends MovieException {

    public MovieRatingAlreadyExistsException() {
        super(ResponseCode.MOVIE_RATING_ALREADY_EXISTS);
    }
}
