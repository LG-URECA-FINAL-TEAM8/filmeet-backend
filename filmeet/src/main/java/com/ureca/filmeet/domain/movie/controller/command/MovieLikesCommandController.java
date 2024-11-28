package com.ureca.filmeet.domain.movie.controller.command;

import com.ureca.filmeet.domain.movie.service.command.MovieLikesCommandService;
import com.ureca.filmeet.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class MovieLikesCommandController {

    private final MovieLikesCommandService movieLikesCommandService;

    @PostMapping("/movies/{movieId}")
    public void movieLikes(
            @PathVariable("movieId") Long movieId,
            @AuthenticationPrincipal User user
    ) {
        movieLikesCommandService.movieLikes(movieId, user.getId());
    }

    @DeleteMapping("/cancel/movies/{movieId}")
    public void movieLikesCancel(
            @PathVariable("movieId") Long movieId,
            @AuthenticationPrincipal User user
    ) {
        movieLikesCommandService.movieLikesCancel(movieId, user.getId());
    }
}
