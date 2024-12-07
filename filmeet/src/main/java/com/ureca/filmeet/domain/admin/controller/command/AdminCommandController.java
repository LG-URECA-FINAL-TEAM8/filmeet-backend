package com.ureca.filmeet.domain.admin.controller.command;

import com.ureca.filmeet.domain.admin.dto.request.AddMoviesRequest;
import com.ureca.filmeet.domain.admin.dto.request.UpdateMovieLikeCountRequest;
import com.ureca.filmeet.domain.admin.dto.request.UpdateMovieRequest;
import com.ureca.filmeet.domain.movie.service.command.MovieCommandService;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.domain.review.service.command.ReviewCommandService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_MOVIE_ADMIN') or hasRole('ROLE_REVIEW_ADMIN')")
public class AdminCommandController {
    private final MovieQueryService movieQueryService;
    private final MovieCommandService movieCommandService;
    private final ReviewCommandService reviewCommandService;

    @PostMapping("/movies/add")
    @PreAuthorize("hasAuthority('MOVIE_CREATE_AUTHORITY')")
    public ResponseEntity<?> addMovies(@RequestBody List<AddMoviesRequest> requests) {
        movieCommandService.addMovies(requests);
        return ApiResponse.okWithoutData();
    }

    @PreAuthorize("hasAuthority('MOVIE_DELETE_AUTHORITY')")
    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long movieId) {
        movieCommandService.deleteMovie(movieId);
        return ApiResponse.okWithoutData();
    }

    @PreAuthorize("hasAuthority('MOVIE_CREATE_AUTHORITY')")
    @PatchMapping("/movies/{movieId}/likes")
    public ResponseEntity<?> updateMovieLikeCount(@PathVariable Long movieId, @RequestBody UpdateMovieLikeCountRequest request) {
        movieCommandService.updateLikeCount(movieId, request);
        return ApiResponse.okWithoutData();
    }

    @PreAuthorize("hasAuthority('MOVIE_UPDATE_AUTHORITY')")
    @PutMapping("/movies/{movieId}")
    public ResponseEntity<?> updateMovie(@PathVariable Long movieId, @RequestBody UpdateMovieRequest request) {
        movieCommandService.updateMovie(movieId, request);
        return ApiResponse.okWithoutData();
    }

    @PreAuthorize("hasAuthority('REVIEW_BLIND_AUTHORITY')")
    @PatchMapping("/reviews/{reviewId}/blind")
    public ResponseEntity<?> blindReview(@PathVariable Long reviewId) {
        reviewCommandService.blindReview(reviewId);
        return ApiResponse.okWithoutData();
    }

    // TODO [eastsage]: 영화 순위 가중치 수정 기능
}
