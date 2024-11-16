package com.ureca.filmeet.domain.movie.controller;

import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.service.MovieQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.global.exception.ExceptionCode;
import java.time.LocalDate;
import java.util.List;
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
}