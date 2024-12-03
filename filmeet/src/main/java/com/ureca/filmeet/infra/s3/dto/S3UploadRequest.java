package com.ureca.filmeet.infra.s3.dto;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record S3UploadRequest(
        MultipartFile file
) {
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp", "svg");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public void validate() throws FileUploadException {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("파일이 비어있습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("파일 크기는 5MB를 초과할 수 없습니다.");
        }
        if (!hasAllowedExtension(file.getOriginalFilename())) {
            throw new FileUploadException("지원하지 않는 파일 형식입니다.");
        }
    }

    private boolean hasAllowedExtension(String fileName) {
        return ALLOWED_EXTENSIONS.stream()
                .anyMatch(ext -> fileName.toLowerCase().endsWith("." + ext));
    }
}
