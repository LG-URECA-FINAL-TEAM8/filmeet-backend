package com.ureca.filmeet.domain.user.exception;

import com.ureca.filmeet.global.exception.GlobalException;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import lombok.Getter;

@Getter
public abstract class UserException extends GlobalException {

    private final ResponseCode errorExceptionCode;

    public UserException(ResponseCode errorExceptionCode) {
        super(errorExceptionCode);
        this.errorExceptionCode = errorExceptionCode;
    }
}
