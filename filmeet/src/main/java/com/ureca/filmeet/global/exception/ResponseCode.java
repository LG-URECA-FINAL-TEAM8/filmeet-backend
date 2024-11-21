package com.ureca.filmeet.global.exception;

import lombok.Getter;

@Getter
public enum ResponseCode {

    // Success Codes
    SUCCESS(200, "Success"),
    CREATED(201, "Resource Created"),

    // Client Error Codes
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),

    // Server Error Codes
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}