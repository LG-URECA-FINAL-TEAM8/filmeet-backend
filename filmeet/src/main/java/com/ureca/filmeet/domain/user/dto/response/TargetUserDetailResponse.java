package com.ureca.filmeet.domain.user.dto.response;

import com.ureca.filmeet.domain.user.entity.Role;


public record TargetUserDetailResponse(
        Long id,
        String username,
        Role role,
        String nickname,
        String profileImage,
        boolean isFirstLogin,
        Integer age,
        String mbti,
        int totalMovieLikes,
        int totalCollections,
        int totalGames,
        Integer reviewCount,
        Integer movieRatingCount,
        long followerCount,
        long followingCount, boolean isFollowing) {
}