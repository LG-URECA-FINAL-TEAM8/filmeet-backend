package com.ureca.filmeet.global.notification.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException() {
        super(ResponseCode.NOTIFICATION_NOT_FOUND);
    }
}
