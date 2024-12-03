package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.GlobalException;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import lombok.Getter;

@Getter
public abstract class GameValidationException extends GlobalException {

    private final ResponseCode errorExceptionCode;

    public GameValidationException (ResponseCode errorExceptionCode) {
        super(errorExceptionCode.getMessage());
        this.errorExceptionCode = errorExceptionCode;
    }
}