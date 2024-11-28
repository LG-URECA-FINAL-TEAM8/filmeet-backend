package com.ureca.filmeet.domain.game.dto.response;

import com.ureca.filmeet.domain.game.entity.Game;
import com.ureca.filmeet.domain.game.entity.GameStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record GameDetailResponse(
        Long id,
        String title,
        GameStatus status,
        Integer totalRounds,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime createdAt,
        List<RoundMatchResponse> matches
) {
    public static GameDetailResponse from(Game game) {
        return new GameDetailResponse(
                game.getId(),
                game.getTitle(),
                game.getStatus(),
                game.getTotalRounds(),
                game.getStartDate(),
                game.getEndDate(),
                game.getCreatedAt(),
                game.getMatches().stream()
                        .map(RoundMatchResponse::from)
                        .collect(Collectors.toList())
        );
    }
}
