package com.ureca.filmeet.domain.user.entity;

import java.util.Set;

public class PrivilegeSets {
    // 관리자용 영화 관련 권한 집합
    public static final Set<Permission> ADMIN_MOVIE_PRIVILEGES = Set.of(
            Permission.MOVIE_CREATE,
            Permission.MOVIE_UPDATE,
            Permission.MOVIE_DELETE,
            Permission.MOVIE_RECOMMEND
    );

    // 관리자용 외부 API 관련 권한 집합
    public static final Set<Permission> ADMIN_EXTERNAL_API_PRIVILEGES = Set.of(
            Permission.EXTERNAL_API_READ
    );

    // 관리자용 리뷰 관련 권한 집합
    public static final Set<Permission> ADMIN_REVIEW_PRIVILEGES = Set.of(
            Permission.REVIEW_READ_ALL,
            Permission.REVIEW_BLIND
    );

    // 성인 유저 추가 권한 집합
    public static final Set<Permission> ADULT_USER_PRIVILEGES = Set.of(
            Permission.ADULT_READ
    );

    // 일반 유저가 가지는 공통 권한 집합
    public static final Set<Permission> USER_COMMON_PRIVILEGES = Set.of(
            Permission.COMMON_READ,
            Permission.COMMON_CREATE,
            Permission.COMMON_UPDATE,
            Permission.COMMON_DELETE
    );

}
