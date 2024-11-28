package com.ureca.filmeet.global.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidPasswordException extends AuthenticationException {
    public InvalidPasswordException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidPasswordException(String msg) {
        super(msg);
    }
}
