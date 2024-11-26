package com.ureca.filmeet.domain.movie.dto.response;

import com.querydsl.core.annotations.QueryProjection;
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
}