package com.ureca.filmeet.domain.movie.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class MovieRecommendationException extends MovieException {

    public MovieRecommendationException(ResponseCode errorExceptionCode) {
        super(errorExceptionCode);
    }
}
