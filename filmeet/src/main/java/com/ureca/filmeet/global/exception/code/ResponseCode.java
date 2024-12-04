package com.ureca.filmeet.global.exception.code;

import lombok.Getter;

@Getter
public enum ResponseCode {

    // Success Codes
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
    SUCCESS(200, "Success"),
    CREATED(201, "Resource Created"),

    // Client Error Codes
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
    NOT_FOUND_USER2(3, ""),

    // Game domain Error Codes
    GAME_NOT_FOUND(11201, "해당 게임을 찾을 수 없습니다."),
    GAME_ALREADY_COMPLETED(11202, "이미 완료된 게임입니다."),
    GAME_ABANDONED(11203, "중단된 게임입니다."),
    GAME_MATCH_NOT_FOUND(11204, "해당 매치를 찾을 수 없습니다."),
    GAME_INVALID_ROUND(11205, "잘못된 라운드 정보입니다."),
    GAME_INVALID_WINNER_SELECTION(11206, "잘못된 승자 선택입니다."),
    GAME_NOT_OWNER(11207, "게임의 소유자가 아닙니다."),
    GAME_ALREADY_HAS_WINNER(11208, "이미 승자가 선택된 매치입니다."),
    GAME_RESULT_NOT_FOUND(11209, "게임 결과를 찾을 수 없습니다."),
    GAME_TITLE_EMPTY(11501, "게임 제목은 필수입니다."),
    GAME_ROUNDS_EMPTY(11502, "총 라운드 수는 필수입니다."),
    GAME_ROUNDS_TOO_SMALL(11503, "최소 2강 이상이어야 합니다."),
    GAME_ROUNDS_TOO_LARGE(11504, "최대 16강까지 가능합니다."),
    GAME_SELECTED_MOVIE_EMPTY(11505, "선택한 영화 ID는 필수입니다."),

    // Follow domain Error Codes (도메인:12)
    FOLLOW_ALREADY_EXISTS(12201, "이미 팔로우한 사용자입니다."),
    FOLLOW_NOT_FOUND(12202, "팔로우 관계를 찾을 수 없습니다."),
    FOLLOW_USER_NOT_FOUND(12203, "팔로우할 사용자를 찾을 수 없습니다."),
    SELF_FOLLOW_NOT_ALLOWED(12204, "자기 자신을 팔로우할 수 없습니다."),

    // Notification domain Error Codes (도메인:13)
    NOTIFICATION_NOT_FOUND(13201, "알림을 찾을 수 없습니다."),
    NOTIFICATION_ACCESS_DENIED(13202, "해당 알림에 접근 권한이 없습니다."),
    INVALID_FCM_TOKEN(13203, "유효하지 않은 FCM 토큰입니다."),
    FCM_TOKEN_NOT_FOUND(13204, "FCM 토큰을 찾을 수 없습니다."),
    FCM_SEND_FAILED(13205, "알림 전송에 실패했습니다.");

    private final Integer status;
    private final String message;

    ResponseCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}