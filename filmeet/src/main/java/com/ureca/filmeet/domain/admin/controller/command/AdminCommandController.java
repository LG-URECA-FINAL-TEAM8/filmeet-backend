package com.ureca.filmeet.domain.admin.controller.command;

import com.ureca.filmeet.domain.admin.dto.request.AddMoviesRequest;
import com.ureca.filmeet.domain.movie.service.command.MovieCommandService;
import com.ureca.filmeet.domain.movie.service.query.MovieQueryService;
import com.ureca.filmeet.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminCommandController {
    private final MovieQueryService movieQueryService;
    private final MovieCommandService movieCommandService;


    // TODO [eastsage]: 영화 리스트 추가 기능
    @PostMapping("/movies/add")
    public ResponseEntity<?> addMovies(@RequestBody List<AddMoviesRequest> requests) {
        movieCommandService.addMovies(requests);
        return ApiResponse.okWithoutData();
    }

    // TODO [eastsage]: 좋아요 삭제 기능
    // TODO [eastsage]: 영화 리스트 추가 기능
    // TODO [eastsage]: 영화 리스트 삭제 기능
    // TODO [eastsage]: 영화 정보 수정
    // TODO [eastsage]: 영화 순위 가중치 수정 기능
}
