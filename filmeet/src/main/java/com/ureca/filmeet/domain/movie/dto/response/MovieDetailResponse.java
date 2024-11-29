package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
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
        boolean isLiked,
        MyMovieReview myMovieReview,
        MyMovieRating myMovieRating,
        List<String> countries,
        List<GenreType> genres,
        List<PersonnelInfoResponse> personnels,
        List<String> galleryImages,
        SliceResponseDto<GetMovieReviewsResponse> movieReviewsResponses
) {

    public static MovieDetailResponse from(Movie movie, boolean isLiked, MyMovieReview myMovieReview,
                                           MyMovieRating myMovieRating,
                                           List<String> countries, List<GenreType> genres,
                                           List<PersonnelInfoResponse> personnels,
                                           List<String> galleryImages,
                                           SliceResponseDto<GetMovieReviewsResponse> movieReviewsResponses) {

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
                isLiked,
                myMovieReview,
                myMovieRating,
                countries,
                genres,
                personnels,
                galleryImages,
                movieReviewsResponses
        );
    }
}