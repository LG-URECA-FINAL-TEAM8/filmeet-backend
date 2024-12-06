package com.ureca.filmeet.domain.game.dto.response;

import com.ureca.filmeet.domain.game.entity.RoundMatch;
import com.ureca.filmeet.domain.movie.dto.response.MoviesRoundmatchResponse;

import java.time.LocalDateTime;

public record RoundMatchResponse(
        Long id,
        Long gameId,
        Long userId,
        MoviesRoundmatchResponse movie1,
        MoviesRoundmatchResponse movie2,
        String winner,
        Integer roundNumber,
        LocalDateTime createdAt
) {
    public static RoundMatchResponse from(RoundMatch match, Integer commentCounts1, Integer commentCounts2) {
        return new RoundMatchResponse(
                match.getId(),
                match.getGame().getId(),
                match.getUser().getId(),
                MoviesRoundmatchResponse.forGameDetail(match.getMovie1(), commentCounts1),
                MoviesRoundmatchResponse.forGameDetail(match.getMovie2(), commentCounts2),
                match.getWinner() != null ? String.valueOf(match.getWinner().getId()) : null,  // null 체크 추가
                match.getRoundNumber(),
                match.getCreatedAt()
        );
    }
}
