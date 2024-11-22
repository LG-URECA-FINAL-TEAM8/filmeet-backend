package com.ureca.filmeet.domain.movie.controller.query;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.MovieDetailResponse;
import com.ureca.filmeet.domain.movie.dto.response.MovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import com.ureca.filmeet.domain.movie.dto.response.RecommendationMoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.repository.BoxOfficeCacheStore;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieRankingsQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieRecommendationQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieUpcomingQueryService;
import com.ureca.filmeet.domain.movie.service.query.MoviesSearchService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieQueryController {

    private final MovieQueryService movieQueryService;
    private final BoxOfficeCacheStore boxOfficeCacheStore;
    private final MoviesSearchService moviesSearchService;
    private final MovieUpcomingQueryService movieUpcomingQueryService;
    private final MovieRankingsQueryService movieRankingsQueryService;
    private final MovieRecommendationQueryService movieRecommendationQueryService;

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<UpcomingMoviesResponse>>> getUpcomingMovies(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {

        LocalDate now = LocalDate.now();
        int defaultYear = year != null ? year : now.getYear();
        int defaultMonth = month != null ? month : now.getMonthValue();

        return ApiResponse.ok(movieUpcomingQueryService.getUpcomingMovies(defaultYear, defaultMonth));
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
            @PathVariable("userId") Long userId) {
        List<RecommendationMoviesResponse> moviesRecommendation = movieRecommendationQueryService.getMoviesRecommendation(
                userId);
        return ApiResponse.ok(moviesRecommendation);
    }

    @GetMapping("/search/genre")
    public ResponseEntity<ApiResponse<Page<MoviesSearchByGenreResponse>>> searchMoviesByGenre(
            @RequestParam(value = "genreTypes", required = false) List<GenreType> genreTypes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MoviesSearchByGenreResponse> moviesSearchByGenreResponses = moviesSearchService.searchMoviesByGenre(
                genreTypes, page, size);
        return ApiResponse.ok(moviesSearchByGenreResponses);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieDetailResponse>> getMovieDetail(@PathVariable("movieId") Long movieId) {
        MovieDetailResponse movieDetail = movieQueryService.getMovieDetail(movieId);
        return ApiResponse.ok(movieDetail);
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
}