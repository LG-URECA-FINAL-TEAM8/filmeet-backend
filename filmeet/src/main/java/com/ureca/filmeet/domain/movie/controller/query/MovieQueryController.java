package com.ureca.filmeet.domain.movie.controller.query;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.MovieDetailResponse;
import com.ureca.filmeet.domain.movie.dto.response.MovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesRandomResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesRatingWithRatingCountResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import com.ureca.filmeet.domain.movie.dto.response.RecommendationMoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.repository.querydsl.SliceWithCount;
import com.ureca.filmeet.domain.movie.service.query.BoxOfficeRedisQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieRecommendationQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieUpcomingQueryService;
import com.ureca.filmeet.domain.movie.service.query.MoviesRankingsRedisQueryService;
import com.ureca.filmeet.domain.movie.service.query.MoviesSearchService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.common.dto.SliceResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
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
    private final BoxOfficeRedisQueryService boxOfficeRedisQueryService;
    private final MoviesSearchService moviesSearchService;
    private final MovieUpcomingQueryService movieUpcomingQueryService;
    private final MovieRecommendationQueryService movieRecommendationQueryService;
    private final MoviesRankingsRedisQueryService moviesRankingsRedisQueryService;

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
        List<Map<String, String>> boxOfficeMovies = boxOfficeRedisQueryService.getBoxOfficeMovies();
        return ApiResponse.ok(boxOfficeMovies);
    }

    @GetMapping("/rankings")
    public ResponseEntity<ApiResponse<List<MoviesRankingsResponse>>> getMoviesRankings() {
        List<MoviesRankingsResponse> moviesRankings = moviesRankingsRedisQueryService.getMoviesRankings();
        return ApiResponse.ok(moviesRankings);
    }

    @GetMapping("/recommendation/users/{userId}")
    public ResponseEntity<ApiResponse<List<RecommendationMoviesResponse>>> getMoviesRecommendation(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 100) Pageable pageable
    ) {
        List<RecommendationMoviesResponse> moviesRecommendation = movieRecommendationQueryService.getMoviesRecommendation(
                userId, pageable
        );
        return ApiResponse.ok(moviesRecommendation);
    }

    @GetMapping("/search/genre")
    public ResponseEntity<ApiResponse<SliceResponseDto<MoviesSearchByGenreResponse>>> searchMoviesByGenre(
            @RequestParam(value = "genreTypes", required = false) List<GenreType> genreTypes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Slice<MoviesSearchByGenreResponse> moviesSearchByGenreResponses = moviesSearchService.searchMoviesByGenre(
                genreTypes, page, size);
        return ApiResponse.ok(SliceResponseDto.of(moviesSearchByGenreResponses));
    }

    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<SliceResponseDto<MovieSearchByTitleResponse>>> searchMoviesByTitle(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Slice<MovieSearchByTitleResponse> movieSearchByTitleResponses = moviesSearchService.searchMoviesByTitle(keyword,
                page, size);
        return ApiResponse.ok(SliceResponseDto.of(movieSearchByTitleResponses));
    }

    @GetMapping("/detail/{movieId}")
    public ResponseEntity<ApiResponse<MovieDetailResponse>> getMovieDetail(
            @PathVariable("movieId") Long movieId,
            @AuthenticationPrincipal User user
    ) {
        Long userId = (user != null) ? user.getId() : null;
        MovieDetailResponse movieDetail = movieQueryService.getMovieDetail(movieId, userId);
        return ApiResponse.ok(movieDetail);
    }

    @GetMapping("/rating")
    public ResponseEntity<ApiResponse<MoviesRatingWithRatingCountResponse>> getMoviesByGenre(
            @RequestParam(value = "genreType", required = false) GenreType genreType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        SliceWithCount<MoviesResponse> moviesResponse = movieQueryService.getMoviesByGenre(
                genreType,
                page,
                size,
                user.getId()
        );
        MoviesRatingWithRatingCountResponse moviesRatingWithRatingCountResponse = MoviesRatingWithRatingCountResponse.of(
                moviesResponse);
        return ApiResponse.ok(moviesRatingWithRatingCountResponse);
    }

    @GetMapping("/random")
    public ResponseEntity<ApiResponse<SliceResponseDto<MoviesRandomResponse>>> getRandomMovies(
            @PageableDefault(size = 10) Pageable pageable
    ) {

        Slice<MoviesRandomResponse> moviesRandomResponses = movieQueryService.getRandomMovies(pageable);
        return ApiResponse.ok(SliceResponseDto.of(moviesRandomResponses));
    }
}