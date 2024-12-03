package com.ureca.filmeet.domain.follow.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class SelfFollowNotAllowedException extends FollowException {
    public SelfFollowNotAllowedException() {
        super(ResponseCode.SELF_FOLLOW_NOT_ALLOWED);
    }
}
