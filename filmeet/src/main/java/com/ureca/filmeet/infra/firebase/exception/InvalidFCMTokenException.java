package com.ureca.filmeet.infra.firebase.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;
import com.ureca.filmeet.global.notification.exception.NotificationException;

public class InvalidFCMTokenException extends NotificationException {
    public InvalidFCMTokenException() {
        super(ResponseCode.INVALID_FCM_TOKEN);
    }
}
