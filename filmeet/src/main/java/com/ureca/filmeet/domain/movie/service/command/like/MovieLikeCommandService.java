package com.ureca.filmeet.domain.movie.service.command.like;

public interface MovieLikeCommandService {

    void movieLikes(Long movieId, Long userId);

    void movieLikesCancel(Long movieId, Long userId);
}
