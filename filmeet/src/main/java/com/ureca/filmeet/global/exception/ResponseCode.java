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
    INVALID_PASSWORD(401, "Invalid Password"),
    INVALID_TOKEN(401, "Invalid Token"),
    ACCESS_TOKEN_EXPIRED(401, "Access token has expired. Use refresh token to get a new access token."),
    REFRESH_TOKEN_EXPIRED(401, "Refresh Token has expired. Login again"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),

    // Server Error Codes
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    // S3 Error Codes
    S3_UPLOAD_FAILED(500, "[101001] File upload failed"),
    S3_DOWNLOAD_FAILED(500, "[101002] File download failed");


    private final Integer status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}