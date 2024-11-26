package com.ureca.filmeet.domain.movie.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.ureca.filmeet.domain.movie.entity.Movie;
import java.time.LocalDate;

public record MoviesResponse(

        Long movieId,
        String title,
        String posterUrl,
        LocalDate releaseDate
) {

    @QueryProjection
    public MoviesResponse {
    }

    public static MoviesResponse of(Movie movie) {

        return new MoviesResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getReleaseDate()
        );
    }
}
