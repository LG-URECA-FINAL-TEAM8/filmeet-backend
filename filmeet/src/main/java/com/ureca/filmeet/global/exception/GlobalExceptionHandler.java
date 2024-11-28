package com.ureca.filmeet.global.exception;

import com.ureca.filmeet.global.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
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

    // 모든 예외 처리 (fallback)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGlobalException(Exception ex) {
//
//        return ApiResponse.internalServerError();
//    }
}