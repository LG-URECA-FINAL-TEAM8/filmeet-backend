package com.ureca.filmeet.domain.follow.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class FollowUserNotFoundException extends FollowException {
    public FollowUserNotFoundException() {
        super(ResponseCode.FOLLOW_USER_NOT_FOUND);
    }
}
