package com.ureca.filmeet.domain.follow.dto.response;

import com.ureca.filmeet.domain.follow.entity.Follow;
import com.ureca.filmeet.domain.user.entity.User;

public record FollowingResponse(
        Long id,            // 유저 ID
        String nickname,    // 닉네임
        String profileImage // 프로필 이미지 URL
) {
    public static FollowingResponse from(Follow follow) {
        User following = follow.getFollowing();
        return new FollowingResponse(
                following.getId(),
                following.getNickname(),
                following.getProfileImage()
        );
    }
}
