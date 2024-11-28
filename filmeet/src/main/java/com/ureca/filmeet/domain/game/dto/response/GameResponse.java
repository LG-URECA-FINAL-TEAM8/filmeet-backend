package com.ureca.filmeet.domain.game.dto.response;

import com.ureca.filmeet.domain.game.entity.Game;
import com.ureca.filmeet.domain.game.entity.GameStatus;

import java.time.LocalDateTime;

public record GameResponse(
        Long id,
        String title,
        GameStatus status,
        Integer totalRounds,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime createdAt
) {
    public static GameResponse from(Game game) {
        return new GameResponse(
                game.getId(),
                game.getTitle(),
                game.getStatus(),
                game.getTotalRounds(),
                game.getStartDate(),
                game.getEndDate(),
                game.getCreatedAt()
        );
    }
}
