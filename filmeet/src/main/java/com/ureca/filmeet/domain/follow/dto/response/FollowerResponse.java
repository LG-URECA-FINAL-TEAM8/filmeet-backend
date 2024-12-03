package com.ureca.filmeet.domain.follow.dto.response;

import com.ureca.filmeet.domain.follow.entity.Follow;
import com.ureca.filmeet.domain.user.entity.User;

public record FollowerResponse(
        Long id,            // 유저 ID
        String nickname,    // 닉네임
        String profileImage // 프로필 이미지 URL
) {
    public static FollowerResponse from(Follow follow) {
        User follower = follow.getFollower();
        return new FollowerResponse(
                follower.getId(),
                follower.getNickname(),
                follower.getProfileImage()
        );
    }
}