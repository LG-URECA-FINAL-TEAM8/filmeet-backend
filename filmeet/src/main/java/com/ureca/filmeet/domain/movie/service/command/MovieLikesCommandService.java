package com.ureca.filmeet.domain.movie.service.command;

import com.ureca.filmeet.domain.genre.entity.enums.GenreScoreAction;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.repository.MovieLikesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieLikesCommandService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieLikesRepository movieLikesRepository;
    private final GenreScoreRepository genreScoreRepository;

    public void movieLikes(Long movieId, Long userId) {
        Movie movie = movieRepository.findMovieWithGenreByMovieId(movieId)
                .orElseThrow(() -> new RuntimeException("no movie"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("no user"));

        MovieLikes movieLikes = MovieLikes.builder()
                .movie(movie)
                .user(user)
                .build();
        movieLikesRepository.save(movieLikes);

        List<Long> genreIds = Optional.ofNullable(movie.getMovieGenres())
                .orElse(Collections.emptyList())
                .stream()
                .map(movieGenre -> movieGenre.getGenre().getId())
                .toList();

        genreScoreRepository.bulkUpdateGenreScores(
                GenreScoreAction.LIKE.getWeight(),
                genreIds,
                userId
        );

        movie.addLikeCounts();
    }
}
