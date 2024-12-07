package com.ureca.filmeet.domain.user.entity;

import lombok.Getter;

@Getter
public enum Permission {
    // 공통 권한
    COMMON_READ("COMMON_READ_AUTHORITY"),
    COMMON_CREATE("COMMON_CREATE_AUTHORITY"),
    COMMON_UPDATE("COMMON_UPDATE_AUTHORITY"),
    COMMON_DELETE("COMMON_DELETE_AUTHORITY"),

    // 영화 관련 권한
    MOVIE_CREATE("MOVIE_CREATE_AUTHORITY"),
    MOVIE_UPDATE("MOVIE_UPDATE_AUTHORITY"),
    MOVIE_DELETE("MOVIE_DELETE_AUTHORITY"),

    // 리뷰 관련 권한 (예: 모든 리뷰 조회, 블라인드 처리)
    REVIEW_READ_ALL("REVIEW_READ_ALL_AUTHORITY"),
    REVIEW_BLIND("REVIEW_BLIND_AUTHORITY"),

    // 외부 API 관련 권한
    EXTERNAL_API_READ("EXTERNAL_API_READ_AUTHORITY");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }
}