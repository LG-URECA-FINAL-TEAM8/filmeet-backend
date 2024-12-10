package com.ureca.filmeet.domain.admin.dto.response;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.entity.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AdminMovieResponse(
        Long id,
        String title,
        String posterUrl,
        Integer likeCounts,
        BigDecimal averageRating,
        List<GenreType> genreTypes,
        LocalDate releaseDate
//        List<String> galleries
) {
    public static AdminMovieResponse fromEntity(Movie movie) {
        return new AdminMovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getLikeCounts() != null ? movie.getLikeCounts() : 0,
                movie.getAverageRating() != null ? movie.getAverageRating() : BigDecimal.ZERO,
                movie.getMovieGenres().stream()
                        .map(movieGenre -> movieGenre.getGenre().getGenreType())
                        .toList(),
                movie.getReleaseDate()
//                movie.getGalleries().stream()
//                        .map(Gallery::getImageUrl)
//                        .toList()
        );
    }
}