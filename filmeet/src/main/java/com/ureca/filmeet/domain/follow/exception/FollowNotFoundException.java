package com.ureca.filmeet.domain.follow.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;


public class FollowNotFoundException extends FollowException {
    public FollowNotFoundException() {
        super(ResponseCode.FOLLOW_NOT_FOUND);
    }
}
