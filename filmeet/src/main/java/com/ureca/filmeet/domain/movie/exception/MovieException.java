package com.ureca.filmeet.domain.movie.exception;

import com.ureca.filmeet.global.exception.GlobalException;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import lombok.Getter;

@Getter
public abstract class MovieException extends GlobalException {

    private final ResponseCode errorExceptionCode;

    public MovieException(ResponseCode errorExceptionCode) {
        super(errorExceptionCode.getMessage());
        this.errorExceptionCode = errorExceptionCode;
    }
}