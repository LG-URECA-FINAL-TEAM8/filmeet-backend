package com.ureca.filmeet.infra.firebase.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;
import com.ureca.filmeet.global.notification.exception.NotificationException;

public class FCMTokenNotFoundException extends NotificationException {
    public FCMTokenNotFoundException() {
        super(ResponseCode.FCM_TOKEN_NOT_FOUND);
    }
}
