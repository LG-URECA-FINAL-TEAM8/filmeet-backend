package com.ureca.filmeet.infra.s3.dto;

import lombok.Builder;

@Builder
public record S3DownloadResponse(
        byte[] data,
        String fileName,
        String contentType,
        Long contentLength
) {
}