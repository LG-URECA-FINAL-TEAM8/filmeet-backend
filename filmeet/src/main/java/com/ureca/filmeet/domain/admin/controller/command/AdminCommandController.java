package com.ureca.filmeet.domain.admin.controller.command;

import com.ureca.filmeet.domain.admin.dto.request.AddMoviesRequest;
import com.ureca.filmeet.domain.admin.dto.request.UpdateMovieLikeCountRequest;
import com.ureca.filmeet.domain.movie.service.command.MovieCommandService;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminCommandController {
    private final MovieQueryService movieQueryService;
    private final MovieCommandService movieCommandService;

    @PostMapping("/movies/add")
    public ResponseEntity<?> addMovies(@RequestBody List<AddMoviesRequest> requests) {
        movieCommandService.addMovies(requests);
        return ApiResponse.okWithoutData();
    }

    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long movieId) {
        movieCommandService.deleteMovie(movieId);
        return ApiResponse.okWithoutData();
    }

    @PatchMapping("/movies/{movieId}/likes")
    public ResponseEntity<?> updateMovieLikeCount(@PathVariable Long movieId, @RequestBody UpdateMovieLikeCountRequest request) {
        movieCommandService.updateLikeCount(movieId, request);
        return ApiResponse.okWithoutData();
    }

    // TODO [eastsage]: 영화 정보 수정
    // TODO [eastsage]: 영화 순위 가중치 수정 기능
}
