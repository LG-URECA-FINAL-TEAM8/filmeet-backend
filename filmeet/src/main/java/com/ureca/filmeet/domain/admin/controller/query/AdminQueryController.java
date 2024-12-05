package com.ureca.filmeet.domain.admin.controller.query;

import com.ureca.filmeet.domain.admin.dto.response.AdminMovieLikesResponse;
import com.ureca.filmeet.domain.admin.dto.response.AdminMovieResponse;
import com.ureca.filmeet.domain.movie.service.query.MovieLikesQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.infra.kmdb.KmdbOpenApiService;
import com.ureca.filmeet.infra.kmdb.dto.KmdbApiResponse;
import com.ureca.filmeet.infra.omdb.dto.OmdbApiResponse;
import com.ureca.filmeet.infra.omdb.dto.OmdbOpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminQueryController {

    private final KmdbOpenApiService kmdbOpenApiService;
    private final OmdbOpenApiService omdbOpenApiService;
    private final MovieQueryService movieQueryService;
    private final MovieLikesQueryService movieLikesQueryService;

    @GetMapping("/movies")
    public ResponseEntity<?> getMovies(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Page<AdminMovieResponse> movieResponses = movieQueryService.getMovies(page, size);
        return ResponseEntity.ok(movieResponses);
    }

    // TODO [eastsage]: 좋아요 조회 기능
    @GetMapping("/movies/{movieId}/likes")
    public ResponseEntity<?> getMovieLikes(@PathVariable Long movieId) {
        List<AdminMovieLikesResponse> responses = movieLikesQueryService.getMovieLikesByMovieId(movieId);
        return ApiResponse.ok(responses);
    }

    @GetMapping("/search/kmdb")
    public ResponseEntity<?> searchMovies(
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String actor) {

        List<KmdbApiResponse> responses = kmdbOpenApiService.searchMovies(director, query, actor);
        return ApiResponse.ok(responses);
    }

    @GetMapping("/search/omdb")
    public ResponseEntity<?> getMovieByTitle(@RequestParam String title) {
        OmdbApiResponse response = omdbOpenApiService.getMovieByTitle(title);
        return ApiResponse.ok(response);
    }

    // TODO [eastsage]: 전체 리뷰 조회 기능
    // TODO [eastsage]: 리뷰 블라인드 기능
}
