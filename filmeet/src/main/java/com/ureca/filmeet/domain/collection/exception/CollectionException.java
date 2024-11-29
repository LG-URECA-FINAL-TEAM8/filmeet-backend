package com.ureca.filmeet.domain.collection.exception;

import com.ureca.filmeet.global.exception.GlobalException;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import lombok.Getter;

@Getter
public abstract class CollectionException extends GlobalException {

    private final ResponseCode errorExceptionCode;

    public CollectionException(ResponseCode errorExceptionCode) {
        super(errorExceptionCode.getMessage());
        this.errorExceptionCode = errorExceptionCode;
    }
}
