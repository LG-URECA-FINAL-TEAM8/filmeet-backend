package com.ureca.filmeet.global.notification.dto;

import com.ureca.filmeet.domain.user.entity.User;

public record UserResponse(
        Long id,
        String nickname,
        String profileImage
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImage()
        );
    }
}
