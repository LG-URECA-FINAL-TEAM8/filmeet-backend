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
    S3_DOWNLOAD_FAILED(500, "[101002] File download failed"),

    // Review domain Error Codes
    REVIEW_ALREADY_EXISTS(10201, "이미 리뷰를 작성했습니다."),
    REVIEW_MOVIE_NOT_FOUND(10202, "리뷰를 작성할 영화가 존재하지 않습니다"),
    REVIEW_USER_NOT_FOUND(10203, "리뷰를 작성할 사용자가 존재하지 않습니다"),
    REVIEW_NOT_FOUND(10204, "등록된 리뷰가 존재하지 않습니다"),
    REVIEW_COMMENT_NOT_FOUND(10205, "리뷰에 등록된 댓글이 존재하지 않습니다"),
    REVIEW_LIKE_ALREADY_EXISTS(10206, "이미 리뷰에 좋아요를 눌렀습니다."),
    REVIEW_LIKE_NOT_FOUND(10207, "취소할 리뷰 좋아요가 없습니다."),

    // Movie domain Error Codes

    // User domain Error Codes
    NOT_FOUND_USER2(3, "");


    private final Integer status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}