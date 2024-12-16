package com.ureca.filmeet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ureca.filmeet.global.exception.code.ResponseCode;
import com.ureca.filmeet.infra.s3.dto.S3DownloadResponse;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(
        Integer code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T data,
        LocalDateTime timestamp
) {
    // 기본 생성자 (ResponseCode 사용)
    public ApiResponse(ResponseCode responseCode) {
        this(responseCode.getStatus(), responseCode.getMessage(), null, LocalDateTime.now());
    }

    // 데이터 포함 생성자
    public ApiResponse(ResponseCode responseCode, T data) {
        this(responseCode.getStatus(), responseCode.getMessage(), data, LocalDateTime.now());
    }

    // 커스텀 에러 메시지 생성자
    public ApiResponse(ResponseCode responseCode, String customMessage) {
        this(responseCode.getStatus(), customMessage, null, LocalDateTime.now());
    }

    public ApiResponse(ResponseCode responseCode, String customMessage, T data) {
        this(responseCode.getStatus(), customMessage, data, LocalDateTime.now());
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(ResponseCode.CREATED.getStatus())
                .body(new ApiResponse<>(ResponseCode.CREATED, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String redirectUrl, T data) {
        return ResponseEntity.created(URI.create(redirectUrl))
                .body(new ApiResponse<>(ResponseCode.CREATED, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.SUCCESS, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> okWithoutData() {
        return ResponseEntity.ok(new ApiResponse<>(ResponseCode.SUCCESS));
    }

    public static <T> ApiResponse<T> error(ResponseCode code) {
        return new ApiResponse<>(code, code.getMessage());
    }

    public static <T> ApiResponse<T> error(ResponseCode code, String customMessage) {
        return new ApiResponse<>(code, customMessage);
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

    public static <T> ResponseEntity<ApiResponse<T>> invalidToken(String customMessage) {
        return ResponseEntity.status(ResponseCode.INVALID_TOKEN.getStatus())
                .body(new ApiResponse<>(ResponseCode.INVALID_TOKEN));
    }

    public static <T> ResponseEntity<ApiResponse<T>> invalidPassword() {
        return ResponseEntity.status(ResponseCode.INVALID_PASSWORD.getStatus())
                .body(new ApiResponse<>(ResponseCode.INVALID_PASSWORD));
    }

    public static <T> ResponseEntity<ApiResponse<T>> accessTokenExpired() {
        return ResponseEntity.status(ResponseCode.ACCESS_TOKEN_EXPIRED.getStatus())
                .body((ApiResponse<T>) new ApiResponse<>(
                        ResponseCode.ACCESS_TOKEN_EXPIRED,
                        Map.of("request_url", "/auth/refresh")));
    }

    public static <T> ResponseEntity<ApiResponse<T>> refreshTokenExpired() {
        return ResponseEntity.status(ResponseCode.REFRESH_TOKEN_EXPIRED.getStatus())
                .body(new ApiResponse<>(ResponseCode.REFRESH_TOKEN_EXPIRED));
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(ResponseCode.NOT_FOUND.getStatus())
                .body(new ApiResponse<>(ResponseCode.NOT_FOUND));
    }

    public static <T> ResponseEntity<ApiResponse<T>> internalServerError() {
        return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(new ApiResponse<>(ResponseCode.INTERNAL_SERVER_ERROR));
    }
    // S3

    public static <T> ResponseEntity<ApiResponse<T>> s3UploadError() {
        return ResponseEntity.status(ResponseCode.S3_UPLOAD_FAILED.getStatus())
                .body(new ApiResponse<>(ResponseCode.S3_UPLOAD_FAILED));
    }

    public static ResponseEntity<ApiResponse<S3DownloadResponse>> s3DownloadError() {
        return ResponseEntity.status(ResponseCode.S3_DOWNLOAD_FAILED.getStatus())
                .body(new ApiResponse<>(ResponseCode.S3_DOWNLOAD_FAILED));
    }

    // 바이너리 데이터를 위한 특별한 응답 메서드
    public static ResponseEntity<byte[]> okBinary(byte[] data, HttpHeaders headers) {
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    public static ResponseEntity<byte[]> binaryError(ResponseCode responseCode) {
        // JSON 형태의 에러 응답 생성
        ApiResponse<Void> errorResponse = new ApiResponse<>(responseCode);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return ResponseEntity.status(responseCode.getStatus())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsBytes(errorResponse));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
                    .body(new byte[0]);
        }
    }
}