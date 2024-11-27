package com.ureca.filmeet.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ureca.filmeet.global.exception.ResponseCode;
import com.ureca.filmeet.infra.s3.dto.S3DownloadResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;

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
    public ApiResponse(Integer code, String message) {
        this(code, message, null, LocalDateTime.now());
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