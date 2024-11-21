package com.ureca.filmeet.domain.movie.controller.query;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import com.ureca.filmeet.domain.movie.dto.response.RecommendationMoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.repository.BoxOfficeCacheStore;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieRecommendationQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
    private final MovieRecommendationQueryService movieRecommendationQueryService;

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<UpcomingMoviesResponse>>> getUpcomingMovies(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {

        LocalDate now = LocalDate.now();
        int defaultYear = year != null ? year : now.getYear();
        int defaultMonth = month != null ? month : now.getMonthValue();

        return ApiResponse.ok(movieQueryService.getUpcomingMovies(defaultYear, defaultMonth));
    }

    @GetMapping("/boxoffice")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getBoxOfficeMovies() {
        List<Map<String, String>> boxOfficeMovies = boxOfficeCacheStore.getBoxOfficeMovies();
        return ApiResponse.ok(boxOfficeMovies);
    }

    @GetMapping("/rankings")
    public ResponseEntity<ApiResponse<List<MoviesRankingsResponse>>> getMoviesRankings() {
        List<MoviesRankingsResponse> moviesRankings = movieQueryService.getMoviesRankings();
        return ApiResponse.ok(moviesRankings);
    }

    @GetMapping("/recommendation/users/{userId}")
    public ResponseEntity<ApiResponse<List<RecommendationMoviesResponse>>> getMoviesRecommendation(
            @PathVariable("userId") Long userId) {
        List<RecommendationMoviesResponse> moviesRecommendation = movieRecommendationQueryService.getMoviesRecommendation(
                userId);
        return ApiResponse.ok(moviesRecommendation);
    }
}