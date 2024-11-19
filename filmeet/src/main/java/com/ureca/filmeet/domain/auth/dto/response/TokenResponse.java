package com.ureca.filmeet.domain.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
