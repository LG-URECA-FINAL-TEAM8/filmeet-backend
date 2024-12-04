package com.ureca.filmeet.global.notification.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class NotificationAccessDeniedException extends NotificationException {
    public NotificationAccessDeniedException() {
        super(ResponseCode.NOTIFICATION_ACCESS_DENIED);
    }
}
