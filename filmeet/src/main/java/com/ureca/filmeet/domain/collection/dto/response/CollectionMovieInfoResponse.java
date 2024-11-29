package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
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

    public static CollectionMovieInfoResponse of(CollectionMovie collectionMovie) {
        return new CollectionMovieInfoResponse(
                collectionMovie.getMovie().getId(),
                collectionMovie.getMovie().getTitle(),
                collectionMovie.getMovie().getPosterUrl(),
                collectionMovie.getMovie().getReleaseDate(),
                collectionMovie.getMovie().getRuntime(),
                collectionMovie.getMovie().getFilmRatings(),
                collectionMovie.getMovie().getAverageRating(),
                collectionMovie.getMovie().getLikeCounts(),
                collectionMovie.getMovie().getRatingCounts()
        );
    }
}