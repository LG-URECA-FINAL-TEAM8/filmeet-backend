package com.ureca.filmeet.domain.movie.controller.query;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.*;
import com.ureca.filmeet.domain.movie.repository.BoxOfficeCacheStore;
import com.ureca.filmeet.domain.movie.service.query.*;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieQueryController {

    private final MovieQueryService movieQueryService;
    private final BoxOfficeCacheStore boxOfficeCacheStore;
    private final MoviesSearchService moviesSearchService;
    private final MovieUpcomingQueryService movieUpcomingQueryService;
    private final MovieRankingsQueryService movieRankingsQueryService;
    private final MovieRecommendationQueryService movieRecommendationQueryService;

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<SliceResponseDto<UpcomingMoviesResponse>>> getUpcomingMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        LocalDate currentDate = LocalDate.now();
        return ApiResponse.ok(SliceResponseDto.of(
                movieUpcomingQueryService.getUpcomingMovies(page, size, currentDate))
        );
    }

    @GetMapping("/boxoffice")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getBoxOfficeMovies() {
        List<Map<String, String>> boxOfficeMovies = boxOfficeCacheStore.getBoxOfficeMovies();
        return ApiResponse.ok(boxOfficeMovies);
    }

    @GetMapping("/rankings")
    public ResponseEntity<ApiResponse<List<MoviesRankingsResponse>>> getMoviesRankings() {
        List<MoviesRankingsResponse> moviesRankings = movieRankingsQueryService.getMoviesRankings();
        return ApiResponse.ok(moviesRankings);
    }

    @GetMapping("/recommendation/users/{userId}")
    public ResponseEntity<ApiResponse<List<RecommendationMoviesResponse>>> getMoviesRecommendation(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "20") int size) {
        List<RecommendationMoviesResponse> moviesRecommendation = movieRecommendationQueryService.getMoviesRecommendation(
                userId, size);
        return ApiResponse.ok(moviesRecommendation);
    }

    @GetMapping("/search/genre")
    public ResponseEntity<ApiResponse<SliceResponseDto<MoviesSearchByGenreResponse>>> searchMoviesByGenre(
            @RequestParam(value = "genreTypes", required = false) List<GenreType> genreTypes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Slice<MoviesSearchByGenreResponse> moviesSearchByGenreResponses = moviesSearchService.searchMoviesByGenre(
                genreTypes, page, size);
        return ApiResponse.ok(SliceResponseDto.of(moviesSearchByGenreResponses));
    }

    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<SliceResponseDto<MovieSearchByTitleResponse>>> searchMoviesByTitle(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Slice<MovieSearchByTitleResponse> movieSearchByTitleResponses = moviesSearchService.searchMoviesByTitle(keyword,
                page, size);
        return ApiResponse.ok(SliceResponseDto.of(movieSearchByTitleResponses));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieDetailResponse>> getMovieDetail(
            @PathVariable("movieId") Long movieId,
            @AuthenticationPrincipal User user
    ) {
        Long userId = (user != null) ? user.getId() : null;
        MovieDetailResponse movieDetail = movieQueryService.getMovieDetail(movieId, userId);
        return ApiResponse.ok(movieDetail);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SliceResponseDto<MoviesResponse>>> getMoviesByGenre(
            @RequestParam(value = "genreType", required = false) GenreType genreType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Slice<MoviesResponse> moviesByGenre = movieQueryService.getMoviesByGenre(genreType, page, size);
        return ApiResponse.ok(SliceResponseDto.of(moviesByGenre));
    }
}