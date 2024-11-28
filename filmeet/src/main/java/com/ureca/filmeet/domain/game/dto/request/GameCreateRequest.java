package com.ureca.filmeet.domain.game.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GameCreateRequest(
        @NotBlank(message = "게임 제목은 필수입니다")
        String title,

        @NotNull(message = "총 라운드 수는 필수입니다")
        @Min(value = 2, message = "최소 2강 이상이어야 합니다")
        @Max(value = 16, message = "최대 16강까지 가능합니다")
        Integer totalRounds
) {}
