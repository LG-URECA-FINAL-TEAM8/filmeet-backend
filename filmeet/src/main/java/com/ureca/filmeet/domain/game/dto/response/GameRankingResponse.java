package com.ureca.filmeet.domain.game.dto.response;


import java.io.Serializable;

public record GameRankingResponse(
        Long id,
        String title,
        String posterUrl,
        Double victoryRatio, // 우승 비율 (최종 우승 횟수 / 전체 게임 수)
        Double winRate,      // 승률 (승리 횟수 / 전체 1대1 대결 수)
        Integer rank             // 랭킹
) implements Serializable {
    public static GameRankingResponse from(Long id, String title, String posterUrl, Double victoryRatio, Double winRate, Integer rank
    ) {
        return new GameRankingResponse(id, title, posterUrl, victoryRatio, winRate, rank);
    }
}
