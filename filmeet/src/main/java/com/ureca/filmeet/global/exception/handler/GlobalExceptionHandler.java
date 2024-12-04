package com.ureca.filmeet.global.exception.handler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ureca.filmeet.domain.collection.exception.CollectionException;
import com.ureca.filmeet.domain.follow.exception.FollowException;
import com.ureca.filmeet.domain.game.exception.GameException;
import com.ureca.filmeet.domain.game.exception.GameRoundsEmptyException;
import com.ureca.filmeet.domain.game.exception.GameRoundsTooLargeException;
import com.ureca.filmeet.domain.game.exception.GameRoundsTooSmallException;
import com.ureca.filmeet.domain.game.exception.GameSelectedMovieEmptyException;
import com.ureca.filmeet.domain.game.exception.GameTitleEmptyException;
import com.ureca.filmeet.domain.movie.exception.MovieException;
import com.ureca.filmeet.domain.review.exception.ReviewException;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.exception.AccessTokenExpiredException;
import com.ureca.filmeet.global.exception.InvalidPasswordException;
import com.ureca.filmeet.global.exception.InvalidRefreshTokenException;
import com.ureca.filmeet.global.exception.JwtAuthenticationException;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import com.ureca.filmeet.global.notification.exception.NotificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<ApiResponse<?>> reviewDomainExceptionHandler(ReviewException e) {
        log.error("review domain exception occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getErrorExceptionCode()));
    }

    // 영화 도메인쪽 예외 처리
    @ExceptionHandler(MovieException.class)
    public ResponseEntity<ApiResponse<?>> movieDomainExceptionHandler(MovieException e) {
        log.error("movie domain exception occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getErrorExceptionCode()));
    }

    // 컬렉션 도메인쪽 예외 처리
    @ExceptionHandler(CollectionException.class)
    public ResponseEntity<ApiResponse<?>> collectionDomainExceptionHandler(CollectionException e) {
        log.error("collection domain exception occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getErrorExceptionCode()));
    }

    // 게임 도메인쪽 예외 처리
    @ExceptionHandler(GameException.class)
    public ResponseEntity<ApiResponse<?>> gameDomainExceptionHandler(GameException e) {
        log.error("game domain exception occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getErrorExceptionCode()));
    }


    // Validation 예외 처리 추가
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            String field = fieldError.getField();
            String defaultMessage = fieldError.getDefaultMessage();

            // 게임 도메인 관련 validation 처리
            if ("title".equals(field)) {
                throw new GameTitleEmptyException();
            } else if ("totalRounds".equals(field)) {
                if (defaultMessage.contains("필수")) {
                    throw new GameRoundsEmptyException();
                } else if (defaultMessage.contains("최소")) {
                    throw new GameRoundsTooSmallException();
                } else if (defaultMessage.contains("최대")) {
                    throw new GameRoundsTooLargeException();
                }
            } else if ("selectedMovieId".equals(field)) {
                throw new GameSelectedMovieEmptyException();
            }
        }

        // 기본 처리
        log.error("validation exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST));
    }

    // Follow 도메인 예외 처리
    @ExceptionHandler(FollowException.class)
    public ResponseEntity<ApiResponse<?>> followDomainExceptionHandler(FollowException e) {
        log.error("follow domain exception occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getErrorExceptionCode()));
    }

    // Notification 도메인 예외 처리
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ApiResponse<?>> handleNotificationException(NotificationException e) {
        log.error("notification exception occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getErrorExceptionCode()));
    }

    // FCM 예외 처리
    @ExceptionHandler(FirebaseMessagingException.class)
    public ResponseEntity<ApiResponse<?>> handleFirebaseMessagingException(FirebaseMessagingException e) {
        log.error("FCM exception occurred: {}", e.getMessage(), e);

        // FCM 에러 코드에 따른 처리
        ResponseCode errorCode = switch (e.getMessagingErrorCode()) {
            case INVALID_ARGUMENT -> ResponseCode.INVALID_FCM_TOKEN;
            case UNREGISTERED -> ResponseCode.FCM_TOKEN_NOT_FOUND;
            default -> ResponseCode.FCM_SEND_FAILED;
        };

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorCode));
    }

    // 모든 예외 처리 (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        log.error("Unhandled exception occurred: ", ex);
        log.error("getCause: {}", ex.getCause());
        return ApiResponse.internalServerError();
    }
}