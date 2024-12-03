package com.ureca.filmeet.domain.follow.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class FollowAlreadyExistsException extends FollowException {
    public FollowAlreadyExistsException() {
        super(ResponseCode.FOLLOW_ALREADY_EXISTS);
    }
}