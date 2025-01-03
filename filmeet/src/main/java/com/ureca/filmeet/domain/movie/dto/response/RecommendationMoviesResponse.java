package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RecommendationMoviesResponse(
        Long movieId,
        String title,
        String posterUrl,
        LocalDate releaseDate,
        Integer runtime,
        Integer likeCounts,
        Integer ratingCounts,
        BigDecimal averageRating,
        FilmRatings filmRatings
) {

    public static RecommendationMoviesResponse of(Movie movie) {
        return new RecommendationMoviesResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getReleaseDate(),
                movie.getRuntime(),
                movie.getLikeCounts() != null ? movie.getLikeCounts() : 0,
                movie.getRatingCounts() != null ? movie.getRatingCounts() : 0,
                movie.getAverageRating() != null ? movie.getAverageRating() : BigDecimal.ZERO,
                movie.getFilmRatings()
        );
    }
}