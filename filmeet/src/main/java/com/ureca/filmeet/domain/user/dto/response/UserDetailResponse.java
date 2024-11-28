package com.ureca.filmeet.domain.user.dto.response;

import com.ureca.filmeet.domain.user.entity.Role;


public record UserDetailResponse(
        Long id,
        String username,
        Role role,
        String nickname,
        String profileImage) {
}