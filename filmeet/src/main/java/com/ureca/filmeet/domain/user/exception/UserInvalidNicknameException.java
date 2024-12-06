package com.ureca.filmeet.domain.user.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class UserInvalidNicknameException extends UserException {
    public UserInvalidNicknameException() {
        super(ResponseCode.USER_NICKNAME_INVALID);
    }
}
