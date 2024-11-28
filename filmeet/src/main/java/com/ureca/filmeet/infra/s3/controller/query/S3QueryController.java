package com.ureca.filmeet.infra.s3.controller.query;

import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.exception.ResponseCode;
import com.ureca.filmeet.infra.s3.dto.S3DownloadResponse;
import com.ureca.filmeet.infra.s3.dto.S3ListResponse;
import com.ureca.filmeet.infra.s3.service.query.S3QueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/s3")
@Tag(name = "S3 API", description = "AWS S3 파일 업로드/다운로드/삭제 API")
@RequiredArgsConstructor
public class S3QueryController {

    private final S3QueryService s3QueryService;
    private static final String DOWNLOAD_PATH_PREFIX = "/s3/download/";

    @GetMapping("/download/**")
    @Operation(summary = "파일 다운로드", description = "S3에서 파일을 다운로드합니다.")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) {
        try {
            String requestUri = request.getRequestURI();
            int startIndex = requestUri.indexOf(DOWNLOAD_PATH_PREFIX) + DOWNLOAD_PATH_PREFIX.length();
            String filePath = requestUri.substring(startIndex);
            S3DownloadResponse downloadDto = s3QueryService.downloadFile(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(downloadDto.contentType()));
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename(downloadDto.fileName(), StandardCharsets.UTF_8)
                    .build());
            headers.setContentLength(downloadDto.contentLength());

            return ApiResponse.okBinary(downloadDto.data(), headers);
        } catch (IOException e) {
            log.error("Failed to download file", e);
            return ApiResponse.binaryError(ResponseCode.S3_DOWNLOAD_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error during file download", e);
            return ApiResponse.binaryError(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<S3ListResponse>> listFiles() throws IOException {
        List<String> strings = s3QueryService.listFiles();
        S3ListResponse s3ListResponse = new S3ListResponse(strings);
        return ApiResponse.ok(s3ListResponse);
    }
}
