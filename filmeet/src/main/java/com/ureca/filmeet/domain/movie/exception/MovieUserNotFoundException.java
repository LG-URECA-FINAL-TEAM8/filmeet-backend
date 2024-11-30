package com.ureca.filmeet.domain.movie.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class MovieUserNotFoundException extends MovieException {

    public MovieUserNotFoundException() {
        super(ResponseCode.MOVIE_USER_NOT_FOUND);
    }
}
