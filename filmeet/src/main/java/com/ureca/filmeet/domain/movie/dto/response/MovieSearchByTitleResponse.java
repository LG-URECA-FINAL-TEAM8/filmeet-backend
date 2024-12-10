package com.ureca.filmeet.domain.movie.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.ureca.filmeet.domain.movie.entity.Movie;
import java.time.LocalDate;

public record MovieSearchByTitleResponse(
        LocalDate releaseDate,
        String title,
        String posterUrl,
        Long movieId
) {

    @QueryProjection
    public MovieSearchByTitleResponse {
    }

    public static MovieSearchByTitleResponse of(Movie movie) {
        return new MovieSearchByTitleResponse(
                movie.getReleaseDate(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getId()
        );
    }
}