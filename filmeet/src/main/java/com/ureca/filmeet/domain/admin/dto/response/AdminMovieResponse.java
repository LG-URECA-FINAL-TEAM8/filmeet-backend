package com.ureca.filmeet.domain.admin.dto.response;

import com.ureca.filmeet.domain.movie.entity.Gallery;
import com.ureca.filmeet.domain.movie.entity.Movie;

import java.math.BigDecimal;
import java.util.List;

public record AdminMovieResponse(
        Long id,
        String title,
        String posterUrl,
        Integer likeCounts,
        BigDecimal averageRating,
        List<String> galleries
) {
    public static AdminMovieResponse fromEntity(Movie movie) {
        return new AdminMovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getLikeCounts() != null ? movie.getLikeCounts() : 0,
                movie.getAverageRating() != null ? movie.getAverageRating() : BigDecimal.ZERO,
                movie.getGalleries().stream()
                        .map(Gallery::getImageUrl)
                        .toList()
        );
    }
}