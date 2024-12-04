package com.ureca.filmeet.infra.firebase.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;
import com.ureca.filmeet.global.notification.exception.NotificationException;

public class FCMSendFailedException extends NotificationException {
    public FCMSendFailedException() {
        super(ResponseCode.FCM_SEND_FAILED);
    }
}