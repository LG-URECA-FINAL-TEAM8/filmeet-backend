package com.ureca.filmeet.domain.admin.controller.query;

import com.ureca.filmeet.global.common.dto.ApiResponse;
import com.ureca.filmeet.infra.kmdb.KmdbOpenApiService;
import com.ureca.filmeet.infra.kmdb.dto.KmdbApiResponse;
import com.ureca.filmeet.infra.omdb.dto.OmdbApiResponse;
import com.ureca.filmeet.infra.omdb.dto.OmdbOpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/search")
public class AdminQueryController {

    private final KmdbOpenApiService kmdbOpenApiService;
    private final OmdbOpenApiService omdbOpenApiService;

    @GetMapping("/kmdb")
    public ResponseEntity<?> searchMovies(
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String actor) {

        List<KmdbApiResponse> responses = kmdbOpenApiService.searchMovies(director, query, actor);
        return ApiResponse.ok(responses);
    }

    @GetMapping("/omdb")
    public ResponseEntity<?> getMovieByTitle(@RequestParam String title) {
        OmdbApiResponse response = omdbOpenApiService.getMovieByTitle(title);
        return ApiResponse.ok(response);
    }


    // TODO [eastsage]: 전체 리뷰 조회 기능
    // TODO [eastsage]: 리뷰 블라인드 기능
    // TODO [eastsage]: 좋아요 조회 기능
}
