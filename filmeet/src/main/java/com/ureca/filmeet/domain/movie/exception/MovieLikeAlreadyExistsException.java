package com.ureca.filmeet.domain.movie.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class MovieLikeAlreadyExistsException extends MovieException {

    public MovieLikeAlreadyExistsException() {
        super(ResponseCode.MOVIE_LIKE_ALREADY_EXISTS);
    }
}
