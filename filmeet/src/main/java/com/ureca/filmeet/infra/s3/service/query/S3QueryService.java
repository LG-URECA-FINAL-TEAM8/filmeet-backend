package com.ureca.filmeet.infra.s3.service.query;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.ureca.filmeet.infra.s3.dto.S3DownloadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3QueryService {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 다운로드
    public S3DownloadResponse downloadFile(String filePath) throws IOException {
        try {
            // 1. 파일 경로 처리 로직을 별도 메서드로 분리
            String decodedFilePath = normalizeFilePath(filePath);
            log.debug("Attempting to download file: {} from bucket: {}", decodedFilePath, bucket);

            // 2. 파일 존재 여부 체크 전에 유효성 검사
            validateFilePath(decodedFilePath);

            // 3. S3 객체 조회 및 데이터 읽기를 try-with-resources로 처리
            S3Object s3Object = amazonS3.getObject(bucket, decodedFilePath);
            byte[] data;
            try (var inputStream = s3Object.getObjectContent()) {
                data = inputStream.readAllBytes();
            }

            String fileName = extractFileName(decodedFilePath);
            String contentType = determineContentType(fileName);

            return S3DownloadResponse.builder()
                    .data(data)
                    .fileName(fileName)
                    .contentType(contentType)
                    .contentLength((long) data.length)
                    .build();

        } catch (AmazonS3Exception e) {
            log.error("AWS S3 error while downloading file: {}", filePath, e);
            throw new IOException("파일을 찾을 수 없습니다.");
        } catch (IOException e) {
            log.error("IO error while downloading file: {}", filePath, e);
            throw new IOException("파일 다운로드 중 오류가 발생했습니다.");
        }
    }

    public List<String> listFiles() throws IOException {
        try {
            log.debug("Listing files from bucket: {}", bucket);
            return amazonS3.listObjectsV2(bucket).getObjectSummaries().stream()
                    .map(S3ObjectSummary::getKey)
                    .toList();
        } catch (AmazonS3Exception e) {
            log.error("Failed to list files from bucket: {}", bucket, e);
            throw new IOException ("파일 목록 조회 중 오류가 발생했습니다");
        }
    }

    private String normalizeFilePath(String filePath) {
        return URLDecoder.decode(filePath, StandardCharsets.UTF_8)
                .replace('\\', '/') // 백슬래시를 슬래시로 변환
                .replaceAll("^/+", "") // 시작 부분의 슬래시 제거
                .replaceAll("/+", "/"); // 연속된 슬래시를 하나로
    }

    private void validateFilePath(String filePath) throws IOException {
        if (!doesFileExist(filePath)) {
            throw new IOException("파일을 찾을 수 없습니다: " + filePath);
        }
    }

    private String extractFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }

    private boolean doesFileExist(String fileName) {
        try {
            return amazonS3.doesObjectExist(bucket, fileName);
        } catch (AmazonS3Exception e) {
            log.warn("Error checking file existence: {} in bucket: {}", fileName, bucket, e);
            return false;
        }
    }

    private String determineContentType(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (extension == null) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
            case "png" -> MediaType.IMAGE_PNG_VALUE;
            case "gif" -> MediaType.IMAGE_GIF_VALUE;
            case "webp" -> "image/webp";
            default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
        };
    }
}
