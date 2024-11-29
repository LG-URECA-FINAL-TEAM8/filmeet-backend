package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CollectionMovieInfoResponse(
        Long movieId,
        String title,
        String posterImage,
        LocalDate releaseDate,
        Integer runtime,
        FilmRatings filmRatings,
        BigDecimal averageRating,
        Integer likeCount,
        Integer ratingCounts
) {
}