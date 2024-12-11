package com.ureca.filmeet.domain.movie.controller.command;

import com.ureca.filmeet.domain.movie.dto.request.DeleteMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.EvaluateMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.ModifyMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.response.ModifyMovieRatingResponse;
import com.ureca.filmeet.domain.movie.service.command.MovieRatingsCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings/movies")
public class MovieRatingsCommandController {

    private final MovieRatingsCommandService movieRatingsCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> evaluateMovieRating(
            @RequestBody EvaluateMovieRatingRequest evaluateMovieRatingRequest,
            @AuthenticationPrincipal User user
    ) {
        movieRatingsCommandService.evaluateMovieRating(evaluateMovieRatingRequest, user.getId());
        return ApiResponse.ok("영화 평가에 참여하셨습니다.");
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<ModifyMovieRatingResponse>> modifyMovieRating(
            @RequestBody ModifyMovieRatingRequest modifyMovieRatingRequest
    ) {
        ModifyMovieRatingResponse modifyMovieRatingResponse = movieRatingsCommandService.modifyMovieRating(
                modifyMovieRatingRequest);
        return ApiResponse.ok(modifyMovieRatingResponse);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteMovieRating(
            @RequestBody DeleteMovieRatingRequest deleteMovieRatingRequest,
            @AuthenticationPrincipal User user
    ) {
        movieRatingsCommandService.deleteMovieRating(deleteMovieRatingRequest, user.getId());
        return ApiResponse.ok("평점을 삭제 했습니다.");
    }
}