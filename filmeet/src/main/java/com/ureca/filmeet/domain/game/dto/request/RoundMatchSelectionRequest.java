package com.ureca.filmeet.domain.game.dto.request;

import jakarta.validation.constraints.NotNull;

public record RoundMatchSelectionRequest(
        @NotNull(message = "선택한 영화 ID는 필수입니다")
        Long selectedMovieId
) {}
