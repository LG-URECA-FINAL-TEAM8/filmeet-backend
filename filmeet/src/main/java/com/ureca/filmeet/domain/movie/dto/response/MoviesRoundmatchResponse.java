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

    public static MoviesRoundmatchResponse of(Movie movie, Integer commentCounts) {

        return new MoviesRoundmatchResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getLikeCounts(),
                commentCounts,
                movie.getRatingCounts()
        );
    }
}
