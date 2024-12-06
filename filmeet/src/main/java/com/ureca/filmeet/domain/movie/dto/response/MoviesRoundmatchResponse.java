package com.ureca.filmeet.domain.movie.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.ureca.filmeet.domain.movie.entity.Movie;

import java.time.LocalDate;

public record MoviesRoundmatchResponse(

        Long movieId,
        String title,
        String posterUrl,
        Integer likeCounts,
        Integer commentCounts,
        Integer ratingCounts
) {

    @QueryProjection
    public MoviesRoundmatchResponse {
    }

    // 게임 상세 조회 API에서 사용하는 메서드
    public static MoviesRoundmatchResponse forGameDetail(Movie movie, Integer commentCounts) {
        return new MoviesRoundmatchResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getLikeCounts(),
                commentCounts,
                movie.getRatingCounts()
        );
    }

    // 추천 API에서 사용하는 메서드
    public static MoviesRoundmatchResponse forRecommendation(Long movieId, String title, String posterUrl, Integer likeCounts, Long commentCounts, Integer ratingCounts) {
        return new MoviesRoundmatchResponse(
                movieId,
                title,
                posterUrl,
                likeCounts,
                commentCounts.intValue(), // Long에서 Integer로 변환
                ratingCounts
        );
    }
}
