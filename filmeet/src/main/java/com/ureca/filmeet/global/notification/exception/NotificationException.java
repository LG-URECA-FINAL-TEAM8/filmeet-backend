package com.ureca.filmeet.global.notification.exception;

import com.ureca.filmeet.global.exception.GlobalException;
import com.ureca.filmeet.global.exception.code.ResponseCode;

public abstract class NotificationException extends GlobalException {
    private final ResponseCode errorExceptionCode;

    public NotificationException(ResponseCode errorExceptionCode) {
        super(errorExceptionCode.getMessage());
        this.errorExceptionCode = errorExceptionCode;
    }
}
