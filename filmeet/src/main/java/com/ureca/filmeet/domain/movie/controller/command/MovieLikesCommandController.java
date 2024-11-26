package com.ureca.filmeet.domain.movie.controller.command;

import com.ureca.filmeet.domain.movie.service.command.MovieLikesCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class MovieLikesCommandController {

    private final MovieLikesCommandService movieLikesCommandService;

    @PostMapping("/movies/{movieId}/users/{userId}")
    public void movieLikes(
            @PathVariable("movieId") Long movieId,
            @PathVariable("userId") Long userId
    ) {
        movieLikesCommandService.movieLikes(movieId, userId);
    }
}
