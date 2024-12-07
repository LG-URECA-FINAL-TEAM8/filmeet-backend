package com.ureca.filmeet.domain.admin.controller.query;

import com.ureca.filmeet.domain.admin.dto.response.AdminMovieLikesResponse;
import com.ureca.filmeet.domain.admin.dto.response.AdminMovieResponse;
import com.ureca.filmeet.domain.admin.dto.response.AdminReviewResponse;
import com.ureca.filmeet.domain.movie.service.query.MovieLikesQueryService;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.domain.review.service.command.ReviewCommandService;
import com.ureca.filmeet.domain.review.service.query.ReviewQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.infra.kmdb.KmdbOpenApiService;
import com.ureca.filmeet.infra.kmdb.dto.KmdbApiResponse;
import com.ureca.filmeet.infra.omdb.dto.OmdbApiResponse;
import com.ureca.filmeet.infra.omdb.dto.OmdbOpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MOVIE_ADMIN') or hasRole('ROLE_REVIEW_ADMIN')")
@RequestMapping("/admin")
public class AdminQueryController {

    private final KmdbOpenApiService kmdbOpenApiService;
    private final OmdbOpenApiService omdbOpenApiService;
    private final MovieQueryService movieQueryService;
    private final MovieLikesQueryService movieLikesQueryService;
    private final ReviewQueryService reviewQueryService;
    private final ReviewCommandService reviewCommandService;

    @GetMapping("/movies")
    public ResponseEntity<?> getMovies(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Page<AdminMovieResponse> movieResponses = movieQueryService.getMovies(page, size);
        return ResponseEntity.ok(movieResponses);
    }

    @GetMapping("/movies/{movieId}/likes")
    public ResponseEntity<?> getMovieLikes(@PathVariable Long movieId) {
        List<AdminMovieLikesResponse> responses = movieLikesQueryService.getMovieLikesByMovieId(movieId);
        return ApiResponse.ok(responses);
    }

    @PreAuthorize("hasAuthority('EXTERNAL_API_READ_AUTHORITY')")
    @GetMapping("/search/kmdb")
    public ResponseEntity<?> searchMovies(
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String actor) {

        List<KmdbApiResponse> responses = kmdbOpenApiService.searchMovies(director, query, actor);
        return ApiResponse.ok(responses);
    }

    @PreAuthorize("hasAuthority('EXTERNAL_API_READ_AUTHORITY')")
    @GetMapping("/search/omdb")
    public ResponseEntity<?> getMovieByTitle(@RequestParam String title) {
        OmdbApiResponse response = omdbOpenApiService.getMovieByTitle(title);
        return ApiResponse.ok(response);
    }

    @PreAuthorize("hasAuthority('REVIEW_READ_ALL_AUTHORITY')")
    @GetMapping("/reviews")
    public ResponseEntity<?> getReviews(
            @RequestParam(required = false) String movieTitle,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) LocalDate createdAt,
            @RequestParam(required = false) LocalDate lastModifiedAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sort
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<AdminReviewResponse> responses = reviewQueryService.getReviewsForAdmin(movieTitle, username, createdAt, lastModifiedAt, sort, pageable);
        return ResponseEntity.ok(responses);
    }
}
