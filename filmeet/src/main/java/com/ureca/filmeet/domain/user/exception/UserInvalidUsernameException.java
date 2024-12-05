package com.ureca.filmeet.domain.user.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class UserInvalidUsernameException extends UserException {

    public UserInvalidUsernameException() {
        super(ResponseCode.USER_USERNAME_INVALID);
    }
}
