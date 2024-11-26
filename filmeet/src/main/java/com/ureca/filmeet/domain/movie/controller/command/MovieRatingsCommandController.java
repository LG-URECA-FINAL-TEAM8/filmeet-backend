package com.ureca.filmeet.domain.movie.controller.command;

import com.ureca.filmeet.domain.movie.dto.request.EvaluateMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.ModifyMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.response.EvaluateMovieRatingResponse;
import com.ureca.filmeet.domain.movie.dto.response.ModifyMovieRatingResponse;
import com.ureca.filmeet.domain.movie.service.command.MovieRatingsCommandService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class MovieRatingsCommandController {

    private final MovieRatingsCommandService movieRatingsCommandService;

    @PostMapping("/movies")
    public ResponseEntity<ApiResponse<EvaluateMovieRatingResponse>> evaluateMovieRating(
            @RequestBody EvaluateMovieRatingRequest evaluateMovieRatingRequest
    ) {
        EvaluateMovieRatingResponse evaluateMovieRatingResponse = movieRatingsCommandService.evaluateMovieRating(
                evaluateMovieRatingRequest);
        return ApiResponse.ok(evaluateMovieRatingResponse);
    }

    @PatchMapping("/movies")
    public ResponseEntity<ApiResponse<ModifyMovieRatingResponse>> modifyMovieRating(
            @RequestBody ModifyMovieRatingRequest modifyMovieRatingRequest
    ) {
        ModifyMovieRatingResponse modifyMovieRatingResponse = movieRatingsCommandService.modifyMovieRating(
                modifyMovieRatingRequest);
        return ApiResponse.ok(modifyMovieRatingResponse);
    }
}
