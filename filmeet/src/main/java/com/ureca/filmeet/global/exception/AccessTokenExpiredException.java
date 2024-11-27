package com.ureca.filmeet.global.exception;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenExpiredException extends AuthenticationException {
    public AccessTokenExpiredException(String message) {
        super(message);
    }
}