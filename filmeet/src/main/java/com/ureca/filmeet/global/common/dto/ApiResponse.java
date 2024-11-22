package com.ureca.filmeet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ureca.filmeet.global.exception.ResponseCode;
import java.net.URI;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(
        Integer code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T data,
        LocalDateTime timestamp
) {
    public ApiResponse(ResponseCode responseCode) {
        this(responseCode.getStatus(), responseCode.getMessage(), null, LocalDateTime.now());
    }

    public ApiResponse(ResponseCode responseCode, T data) {
        this(responseCode.getStatus(), responseCode.getMessage(), data, LocalDateTime.now());
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String redirectUrl, T data) {
        return ResponseEntity.created(URI.create(redirectUrl))
                .body(new ApiResponse<>(ResponseCode.CREATED, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.SUCCESS, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest() {
        return ResponseEntity.status(ResponseCode.BAD_REQUEST.getStatus())
                .body(new ApiResponse<>(ResponseCode.BAD_REQUEST));
    }

    public static <T> ResponseEntity<ApiResponse<T>> forbidden() {
        return ResponseEntity.status(ResponseCode.FORBIDDEN.getStatus())
                .body(new ApiResponse<>(ResponseCode.FORBIDDEN));
    }

    public static <T> ResponseEntity<ApiResponse<T>> unAuthorized() {
        return ResponseEntity.status(ResponseCode.UNAUTHORIZED.getStatus())
                .body(new ApiResponse<>(ResponseCode.UNAUTHORIZED));
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(ResponseCode.NOT_FOUND.getStatus())
                .body(new ApiResponse<>(ResponseCode.NOT_FOUND));
    }

    public static <T> ResponseEntity<ApiResponse<T>> internalServerError() {
        return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR));
    }
}