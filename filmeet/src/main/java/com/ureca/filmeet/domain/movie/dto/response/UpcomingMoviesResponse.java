package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpcomingMoviesResponse(
        String title,
        String posterUrl,
        LocalDate releaseDate,
        Integer runtime,
        Integer likeCounts,
        Integer reviewCounts,
        BigDecimal averageRating,
        FilmRatings filmRatings
) {

    public static UpcomingMoviesResponse of(Movie movie) {
        return new UpcomingMoviesResponse(
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getReleaseDate(),
                movie.getRuntime(),
                movie.getLikeCounts() != null ? movie.getLikeCounts() : 0,
                movie.getReviewCounts() != null ? movie.getReviewCounts() : 0,
                movie.getAverageRating() != null ? movie.getAverageRating() : BigDecimal.ZERO,
                movie.getFilmRatings()
        );
    }
}