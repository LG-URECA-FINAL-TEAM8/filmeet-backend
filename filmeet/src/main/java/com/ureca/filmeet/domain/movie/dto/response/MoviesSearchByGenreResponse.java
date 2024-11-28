package com.ureca.filmeet.domain.movie.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MoviesSearchByGenreResponse(

        Long movieId,
        String title,
        String posterUrl,
        LocalDate releaseDate,
        Integer runtime,
        Integer likeCounts,
        Integer ratingCounts,
        BigDecimal averageRating,
        FilmRatings filmRatings,
        List<GenreType> genreTypes
) {

    @QueryProjection
    public MoviesSearchByGenreResponse {
    }
}
