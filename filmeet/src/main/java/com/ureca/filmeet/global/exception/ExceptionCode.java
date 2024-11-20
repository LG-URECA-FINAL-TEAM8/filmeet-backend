package com.ureca.filmeet.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    // 정상 응답 코드
    OK(200, HttpStatus.OK, "success"),
    CREATED(201, HttpStatus.CREATED, "successfully created");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}