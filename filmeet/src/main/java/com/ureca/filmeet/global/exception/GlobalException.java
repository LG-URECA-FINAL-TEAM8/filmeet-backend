package com.ureca.filmeet.global.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;
import lombok.Getter;

@Getter
public abstract class GlobalException extends RuntimeException {

    private final ResponseCode errorExceptionCode;

    // errorCode에 대한 getter
    // 기본 생성자에서는 일반적인 에러 코드를 사용
    public GlobalException() {
        super(ResponseCode.INTERNAL_SERVER_ERROR.getMessage());
        this.errorExceptionCode = ResponseCode.INTERNAL_SERVER_ERROR;
    }

    // 에러 코드를 지정하는 생성자
    public GlobalException(ResponseCode errorExceptionCode) {
        super(errorExceptionCode.getMessage());
        this.errorExceptionCode = errorExceptionCode;
    }

    // 에러 메시지를 받는 생성자
    public GlobalException(String message) {
        super(message);
        this.errorExceptionCode = ResponseCode.INTERNAL_SERVER_ERROR;
    }

    // 에러 메시지와 원인을 받는 생성자
    public GlobalException(String message, Throwable cause) {
        super(message, cause);
        this.errorExceptionCode = ResponseCode.INTERNAL_SERVER_ERROR;
    }

    // 원인만을 받는 생성자
    public GlobalException(Throwable cause) {
        super(cause);
        this.errorExceptionCode = ResponseCode.INTERNAL_SERVER_ERROR;
    }

    // 에러 코드와 메시지를 받는 생성자
    public GlobalException(ResponseCode errorExceptionCode, String message) {
        super(message);
        this.errorExceptionCode = errorExceptionCode;
    }

    // 에러 코드, 메시지, 원인을 받는 생성자
    public GlobalException(ResponseCode errorExceptionCode, String message, Throwable cause) {
        super(message, cause);
        this.errorExceptionCode = errorExceptionCode;
    }

    // 에러 코드와 원인을 받는 생성자
    public GlobalException(ResponseCode errorExceptionCode, Throwable cause) {
        super(cause);
        this.errorExceptionCode = errorExceptionCode;
    }
}