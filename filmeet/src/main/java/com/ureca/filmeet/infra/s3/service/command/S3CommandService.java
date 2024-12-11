package com.ureca.filmeet.infra.s3.service.command;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ureca.filmeet.infra.s3.dto.S3UploadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3CommandService {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // 파일 업로드
    @Transactional
    public String upload(S3UploadRequest request) throws FileUploadException {
        try {
            request.validate();
            MultipartFile file = request.file();
            String fileName = createFileName(file);

            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));  // 공개 읽기 권한 설정

            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();

            return fileUrl;
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }
    // 파일 삭제
    @Transactional
    public void delete(String fileUrl) throws IOException {
        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
            amazonS3.deleteObject(deleteObjectRequest);

        } catch (AmazonServiceException e) {
            log.error("Failed to delete file: {} with error: {}", fileUrl, e.getMessage());
            throw new IOException("파일 삭제에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while deleting file: {} with error: {}", fileUrl, e.getMessage());
            throw new IOException("파일 삭제 중 예상치 못한 오류가 발생했습니다");
        }
    }

    private String extractFileNameFromUrl(String fileUrl) throws IOException {
        try {
            // URL에서 버킷 이름 이후의 경로를 추출
            String bucketPrefix = bucket + ".s3." + "ap-northeast-2" + ".amazonaws.com/";
            int startIndex = fileUrl.indexOf(bucketPrefix) + bucketPrefix.length();
            String fileName = fileUrl.substring(startIndex);

            // URL 디코딩
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

            log.debug("Extracted file name: {}", fileName);  // 로깅 추가
            return fileName;
        } catch (Exception e) {
            log.error("Error extracting file name from URL: {}", fileUrl);
            throw new IOException("파일 URL 처리 중 오류가 발생했습니다");
        }
    }

    private String createFileName(MultipartFile file) {
        return String.format("%s/%s_%s",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                UUID.randomUUID(),
                file.getOriginalFilename()
        );
    }

    // 직렬화된 Trie 업로드
    public void uploadSerializedTrie(String fileName, byte[] data) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            metadata.setContentType("application/octet-stream");

            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.Private));
        }
    }
}
