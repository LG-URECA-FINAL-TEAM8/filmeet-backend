package com.ureca.filmeet.domain.game.dto.response;

import com.ureca.filmeet.domain.game.entity.RoundMatch;
import com.ureca.filmeet.domain.movie.dto.response.MoviesResponse;

import java.time.LocalDateTime;

public record RoundMatchResponse(
        Long id,
        Long gameId,
        Long userId,
        MoviesResponse movie1,
        MoviesResponse movie2,
        String winner,
        Integer roundNumber,
        LocalDateTime createdAt
) {
    public static RoundMatchResponse from(RoundMatch match) {
        return new RoundMatchResponse(
                match.getId(),
                match.getGame().getId(),
                match.getUser().getId(),
                MoviesResponse.of(match.getMovie1()),
                MoviesResponse.of(match.getMovie2()),
                match.getWinner() != null ? String.valueOf(match.getWinner().getId()) : null,  // null 체크 추가
                match.getRoundNumber(),
                match.getCreatedAt()
        );
    }
}
