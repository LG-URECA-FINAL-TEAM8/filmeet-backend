package com.ureca.filmeet.infra.firebase.dto;

import jakarta.validation.constraints.NotBlank;

public record FCMTokenRequest(
        @NotBlank(message = "FCM 토큰은 필수입니다")
        String token
) {}
