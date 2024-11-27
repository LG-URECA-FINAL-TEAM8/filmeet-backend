package com.ureca.filmeet.infra.s3.etc;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public final class S3Constants {
    public static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String FILE_URL_PREFIX = "https://%s.s3.%s.amazonaws.com/%s";

}
