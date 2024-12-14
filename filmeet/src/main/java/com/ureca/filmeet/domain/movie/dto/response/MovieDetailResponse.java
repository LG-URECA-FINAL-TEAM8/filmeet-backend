package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.entity.Movie;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MovieDetailResponse(
        Long movieId,
        String title,
        String plot,
        String posterUrl,
        LocalDate releaseDate,
        Integer runtime,
        Integer likeCounts,
        Integer ratingCounts,
        BigDecimal averageRating,
        UserMovieInteractionResponse userMovieInteractionResponse,
        List<String> countries,
        List<GenreType> genres,
        List<PersonnelInfoResponse> personnels,
        List<String> galleryImages,
        List<RatingDistributionResponse> ratingDistribution
) {

    public static MovieDetailResponse from(
            Movie movie,
            UserMovieInteractionResponse userMovieInteractionResponse,
            List<String> countries, List<GenreType> genres,
            List<PersonnelInfoResponse> personnels,
            List<String> galleryImages,
            List<RatingDistributionResponse> ratingDistribution
    ) {

        return new MovieDetailResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPlot(),
                movie.getPosterUrl(),
                movie.getReleaseDate(),
                movie.getRuntime(),
                movie.getLikeCounts(),
                movie.getRatingCounts(),
                movie.getAverageRating(),
                userMovieInteractionResponse,
                countries,
                genres,
                personnels,
                galleryImages,
                ratingDistribution
        );
    }
}