package com.ureca.filmeet.domain.auth.dto.response;

public record LoginResponse(
        boolean isFirstLogin,
        TokenResponse tokenResponse
) {
}
