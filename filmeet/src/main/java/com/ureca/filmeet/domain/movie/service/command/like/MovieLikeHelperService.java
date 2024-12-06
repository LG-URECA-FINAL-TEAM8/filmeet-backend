package com.ureca.filmeet.domain.movie.service.command.like;

import com.ureca.filmeet.domain.genre.entity.enums.GenreScoreAction;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.exception.MovieLikeAlreadyExistsException;
import com.ureca.filmeet.domain.movie.exception.MovieNotFoundException;
import com.ureca.filmeet.domain.movie.exception.MovieUserNotFoundException;
import com.ureca.filmeet.domain.movie.repository.MovieLikesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieLikeHelperService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieLikesRepository movieLikesRepository;
    private final GenreScoreRepository genreScoreRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void movieLikes(Long userId, Long movieId) {
        boolean isAlreadyLiked = movieLikesRepository.existsByMovieIdAndUserId(movieId, userId);
        if (isAlreadyLiked) {
            throw new MovieLikeAlreadyExistsException();
        }
        
        Movie movie = movieRepository.findMovieWithGenreByMovieId(movieId)
                .orElseThrow(MovieNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(MovieUserNotFoundException::new);

        MovieLikes movieLikes = MovieLikes.builder()
                .movie(movie)
                .user(user)
                .build();
        movieLikesRepository.save(movieLikes);

        updateGenreScoresForUser(userId, movie, GenreScoreAction.LIKE);

        movie.addLikeCounts();
    }

    private void updateGenreScoresForUser(Long userId, Movie movie, GenreScoreAction genreScoreAction) {
        List<Long> genreIds = Optional.ofNullable(movie.getMovieGenres())
                .orElse(Collections.emptyList())
                .stream()
                .map(movieGenre -> movieGenre.getGenre().getId())
                .toList();

        genreScoreRepository.bulkUpdateGenreScores(
                genreScoreAction.getWeight(),
                genreIds,
                userId
        );
    }
}
