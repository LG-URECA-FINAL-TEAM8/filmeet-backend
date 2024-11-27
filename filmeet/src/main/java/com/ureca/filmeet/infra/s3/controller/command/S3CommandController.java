package com.ureca.filmeet.infra.s3.controller.command;

import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.infra.s3.dto.S3UploadRequest;
import com.ureca.filmeet.infra.s3.dto.S3UploadResponse;
import com.ureca.filmeet.infra.s3.service.command.S3CommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/s3")
@Tag(name = "S3 API", description = "AWS S3 파일 업로드/다운로드/삭제 API")
@RequiredArgsConstructor
public class S3CommandController {

    private final S3CommandService s3CommandService;

    @PostMapping("/upload")
    @Operation(summary = "파일 업로드", description = "AWS S3에 파일을 업로드합니다.")
    public ResponseEntity<ApiResponse<S3UploadResponse>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            S3UploadRequest request = new S3UploadRequest(file);
            S3UploadResponse response = new S3UploadResponse(s3CommandService.upload(request));
            return ApiResponse.ok(response);
        } catch (IOException e) {
            return ApiResponse.s3UploadError();
        }
    }

    @DeleteMapping
    @Operation(summary = "파일 삭제", description = "AWS S3에서 파일을 삭제합니다.")
    public ResponseEntity<String> deleteFile(@RequestParam String fileUrl) {
        try {
            s3CommandService.delete(fileUrl);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("File not found");
        }
    }
}
