package com.ureca.filmeet.global.exception.code;

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
    REVIEW_MOVIE_NOT_FOUND(10202, "리뷰와 관련한 영화가 존재하지 않습니다"),
    REVIEW_USER_NOT_FOUND(10203, "리뷰와 관련한 사용자가 존재하지 않습니다"),
    REVIEW_NOT_FOUND(10204, "등록된 리뷰가 존재하지 않습니다"),
    REVIEW_COMMENT_NOT_FOUND(10205, "리뷰에 등록된 댓글이 존재하지 않습니다"),
    REVIEW_LIKE_ALREADY_EXISTS(10206, "이미 리뷰에 좋아요를 눌렀습니다."),
    REVIEW_LIKE_NOT_FOUND(10207, "취소할 리뷰 좋아요가 없습니다."),

    // Movie domain Error Codes
    MOVIE_LIKE_ALREADY_EXISTS(20201, "이미 영화에 좋아요를 눌렀습니다."),
    MOVIE_NOT_FOUND(20202, "등록된 영화가 존재하지 않습니다."),
    MOVIE_USER_NOT_FOUND(20203, "영화와 관련한 유저가 존재하지 않습니다."),
    MOVIE_LIKE_NOT_FOUND(20204, "취소할 영화 좋아요가 없습니다."),
    MOVIE_RATING_ALREADY_EXISTS(20205, "이미 영화에 별점을 남겼습니다."),
    MOVIE_RATING_NOT_FOUND(20206, "기존 영화 별점이 없습니다."),

    // Collection domain Error Codes
    COLLECTION_USER_NOT_FOUND(30201, "컬렉션과 관련한 유저가 존재하지 않습니다."),
    COLLECTION_NOT_FOUND(30202, "등록된 컬렉션이 존재하지 않습니다."),
    COLLECTION_COMMENT_NOT_FOUND(30203, "컬렉션과 관련한 댓글이 존재하지 않습니다."),
    COLLECTION_LIKE_ALREADY_EXISTS(20204, "이미 컬렉션에 좋아요를 눌렀습니다."),
    COLLECTION_LIKE_NOT_FOUND(20205, "취소할 컬렉션 좋아요가 없습니다."),
    COLLECTION_MOVIES_NOT_FOUND(20206, "컬렉션과 관련한 영화 목록이 존재하지 않습니다."),

    // User domain Error Codes
    NOT_FOUND_USER2(3, "");


    private final Integer status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}