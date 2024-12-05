package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.admin.dto.response.AdminMovieLikesResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.repository.MovieLikesRepository;
import com.ureca.filmeet.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieLikesQueryService {

    private final MovieLikesRepository movieLikesRepository;

    public List<AdminMovieLikesResponse> getMovieLikesByMovieId(Long movieId) {
        List<MovieLikes> movieLikesList = movieLikesRepository.findByMovie_Id(movieId);

        return movieLikesList.stream()
                .map(like -> {
                    Movie movie = like.getMovie();
                    User user = like.getUser();

                    return new AdminMovieLikesResponse(
                            like.getId(),
                            movie.getId(),
                            movie.getTitle(),
                            user.getId(),
                            user.getUsername(),
                            like.getCreatedAt()
                    );
                })
                .toList();
    }
}
