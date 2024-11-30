package com.ureca.filmeet.domain.movie.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class MovieLikeNotFoundException extends MovieException {

    public MovieLikeNotFoundException() {
        super(ResponseCode.MOVIE_LIKE_NOT_FOUND);
    }
}