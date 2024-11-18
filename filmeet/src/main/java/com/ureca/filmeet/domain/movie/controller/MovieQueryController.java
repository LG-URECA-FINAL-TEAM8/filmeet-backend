package com.ureca.filmeet.domain.movie.controller;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.repository.BoxOfficeCacheStore;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.exception.ExceptionCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieQueryController {

    private final MovieQueryService movieQueryService;
    private final BoxOfficeCacheStore boxOfficeCacheStore;

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<UpcomingMoviesResponse>>> getUpcomingMovies(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {

        LocalDate now = LocalDate.now();
        int defaultYear = year != null ? year : now.getYear();
        int defaultMonth = month != null ? month : now.getMonthValue();

        return ApiResponse.ok(ExceptionCode.OK.getCode(),
                movieQueryService.getUpcomingMovies(defaultYear, defaultMonth),
                ExceptionCode.OK.getMessage());
    }

    @GetMapping("/boxoffice")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getBoxOfficeMovies() {
        List<Map<String, String>> boxOfficeMovies = boxOfficeCacheStore.getBoxOfficeMovies();
        return ApiResponse.ok(ExceptionCode.OK.getCode(),
                boxOfficeMovies,
                ExceptionCode.OK.getMessage());
    }

    @GetMapping("/rankings")
    public ResponseEntity<ApiResponse<List<MoviesRankingsResponse>>> getMoviesRankings() {
        List<MoviesRankingsResponse> moviesRankings = movieQueryService.getMoviesRankings();
        return ApiResponse.ok(ExceptionCode.OK.getCode(),
                moviesRankings,
                ExceptionCode.OK.getMessage());
    }
}