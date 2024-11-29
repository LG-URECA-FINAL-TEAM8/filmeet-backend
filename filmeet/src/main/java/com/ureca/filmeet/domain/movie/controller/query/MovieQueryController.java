package com.ureca.filmeet.domain.movie.controller.query;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.MovieDetailResponse;
import com.ureca.filmeet.domain.movie.dto.response.MovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import com.ureca.filmeet.domain.movie.dto.response.RecommendationMoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.repository.BoxOfficeCacheStore;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieRankingsQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieRecommendationQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieUpcomingQueryService;
import com.ureca.filmeet.domain.movie.service.query.MoviesSearchService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        MovieDetailResponse movieDetail = movieQueryService.getMovieDetail(movieId, user.getId());
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