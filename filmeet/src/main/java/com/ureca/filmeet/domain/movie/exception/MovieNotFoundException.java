package com.ureca.filmeet.domain.movie.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class MovieNotFoundException extends MovieException {

    public MovieNotFoundException() {
        super(ResponseCode.MOVIE_NOT_FOUND);
    }
}
