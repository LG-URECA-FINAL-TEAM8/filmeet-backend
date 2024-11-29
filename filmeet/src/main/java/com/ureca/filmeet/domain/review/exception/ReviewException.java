package com.ureca.filmeet.domain.review.exception;

import com.ureca.filmeet.global.exception.GlobalException;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import lombok.Getter;

@Getter
public abstract class ReviewException extends GlobalException {

    private final ResponseCode errorExceptionCode;

    public ReviewException(ResponseCode errorExceptionCode) {
        super(errorExceptionCode.getMessage());
        this.errorExceptionCode = errorExceptionCode;
    }
}