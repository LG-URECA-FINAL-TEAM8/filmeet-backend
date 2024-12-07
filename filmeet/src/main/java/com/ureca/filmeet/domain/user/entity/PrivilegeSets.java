package com.ureca.filmeet.domain.user.entity;

import java.util.Set;

import static com.ureca.filmeet.domain.user.entity.Permission.*;

public class PrivilegeSets {
    // 관리자용 영화 관련 권한 집합
    public static final Set<Permission> ADMIN_MOVIE_PRIVILEGES = Set.of(
            MOVIE_CREATE,
            MOVIE_UPDATE,
            MOVIE_DELETE
    );

    // 관리자용 리뷰 관련 권한 집합
    public static final Set<Permission> ADMIN_REVIEW_PRIVILEGES = Set.of(
            REVIEW_READ_ALL,
            REVIEW_BLIND
    );

    // 관리자용 외부 API 관련 권한 집합
    public static final Set<Permission> ADMIN_EXTERNAL_API_PRIVILEGES = Set.of(
            EXTERNAL_API_READ
    );

    // 일반 유저가 가지는 공통 권한 집합
    public static final Set<Permission> USER_COMMON_PRIVILEGES = Set.of(
            COMMON_READ,
            COMMON_CREATE,
            COMMON_UPDATE,
            COMMON_DELETE
    );
}
