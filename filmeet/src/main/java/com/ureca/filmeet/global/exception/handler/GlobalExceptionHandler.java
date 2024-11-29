package com.ureca.filmeet.global.exception.handler;

import com.ureca.filmeet.domain.review.exception.ReviewException;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.exception.AccessTokenExpiredException;
import com.ureca.filmeet.global.exception.InvalidPasswordException;
import com.ureca.filmeet.global.exception.InvalidRefreshTokenException;
import com.ureca.filmeet.global.exception.JwtAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 인증 실패 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        return ApiResponse.unAuthorized();
    }

    // Access Token 만료 처리
    @ExceptionHandler(AccessTokenExpiredException.class)
    public ResponseEntity<?> handleAccessTokenExpired(AccessTokenExpiredException ex) {
        return ApiResponse.accessTokenExpired();
    }

    // Refresh Token 만료 처리
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleRefreshTokenExpired(InvalidRefreshTokenException ex) {
        return ApiResponse.refreshTokenExpired();
    }

    // 권한 부족 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return ApiResponse.forbidden();
    }

    // Jwt 검증 예외 처리
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        return ApiResponse.invalidToken(ex.getMessage());
    }

    // PW 로그인 예외 처리
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> handleInvalidPasswordException(InvalidPasswordException ex) {
        return ApiResponse.invalidPassword();
    }

    // 리뷰 도메인쪽 예외 처리
    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ApiResponse<?>> reviewAlreadyExistsExceptionHandler(ReviewException e) {
        log.error("ReviewAlreadyExistsException occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(e.getErrorExceptionCode()));
    }

    // 모든 예외 처리 (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        log.error("Unhandled exception occurred: ", ex);
        return ApiResponse.internalServerError();
    }
}