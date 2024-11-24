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
        Integer reviewCounts,
        BigDecimal averageRating,
        boolean isLiked,
        ReviewInfo reviewInfo,
        List<String> countries,
        List<GenreType> genres,
        List<PersonnelInfoResponse> personnels,
        List<String> galleryImages
) {

    public static MovieDetailResponse from(Movie movie, boolean isLiked, ReviewInfo reviewInfo,
                                           List<String> countries, List<GenreType> genres,
                                           List<PersonnelInfoResponse> personnels,
                                           List<String> galleryImages) {

        return new MovieDetailResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getPlot(),
                movie.getPosterUrl(),
                movie.getReleaseDate(),
                movie.getRuntime(),
                movie.getLikeCounts(),
                movie.getReviewCounts(),
                movie.getAverageRating(),
                isLiked,
                reviewInfo,
                countries,
                genres,
                personnels,
                galleryImages
        );
    }
}