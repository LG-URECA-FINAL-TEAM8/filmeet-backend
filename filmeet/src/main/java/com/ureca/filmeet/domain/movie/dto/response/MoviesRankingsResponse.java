package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public record MoviesRankingsResponse(
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

    public static MoviesRankingsResponse of(Movie movie) {
        return new MoviesRankingsResponse(
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

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("movieId", String.valueOf(movieId));
        map.put("title", title);
        map.put("posterUrl", posterUrl);
        map.put("releaseDate", releaseDate != null ? releaseDate.toString() : null);
        map.put("runtime", runtime != null ? runtime.toString() : null);
        map.put("likeCounts", likeCounts != null ? likeCounts.toString() : "0");
        map.put("ratingCounts", ratingCounts != null ? ratingCounts.toString() : "0");
        map.put("averageRating", averageRating != null ? averageRating.toString() : BigDecimal.ZERO.toString());
        map.put("filmRatings", filmRatings != null ? filmRatings.toString() : null);
        return map;
    }

    public static MoviesRankingsResponse mapToMoviesRankingsResponse(Map<String, String> movieRanking) {
        return new MoviesRankingsResponse(
                Long.valueOf(movieRanking.get("movieId")),
                movieRanking.get("title"),
                movieRanking.get("posterUrl"),
                movieRanking.get("releaseDate") != null ? LocalDate.parse(movieRanking.get("releaseDate")) : null,
                movieRanking.get("runtime") != null ? Integer.valueOf(movieRanking.get("runtime")) : null,
                movieRanking.get("likeCounts") != null ? Integer.parseInt(movieRanking.get("likeCounts")) : 0,
                movieRanking.get("ratingCounts") != null ? Integer.parseInt(movieRanking.get("ratingCounts")) : 0,
                movieRanking.get("averageRating") != null ? new BigDecimal(movieRanking.get("averageRating"))
                        : BigDecimal.ZERO,
                movieRanking.get("filmRatings") != null ? FilmRatings.valueOf(movieRanking.get("filmRatings")) : null
        );
    }
}