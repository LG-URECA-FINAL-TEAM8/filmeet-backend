package com.ureca.filmeet.domain.follow.exception;

import com.ureca.filmeet.global.exception.GlobalException;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import lombok.Getter;

@Getter
public abstract class FollowException extends GlobalException {
    private final ResponseCode errorExceptionCode;

    public FollowException(ResponseCode errorExceptionCode) {
        super(errorExceptionCode.getMessage());
        this.errorExceptionCode = errorExceptionCode;
    }
}
