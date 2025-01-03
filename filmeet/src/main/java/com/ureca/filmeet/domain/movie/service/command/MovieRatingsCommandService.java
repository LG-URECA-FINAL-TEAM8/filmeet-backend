package com.ureca.filmeet.domain.movie.service.command;

import com.ureca.filmeet.domain.genre.entity.enums.GenreScoreAction;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.dto.request.DeleteMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.EvaluateMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.ModifyMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.response.ModifyMovieRatingResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import com.ureca.filmeet.domain.movie.exception.MovieNotFoundException;
import com.ureca.filmeet.domain.movie.exception.MovieRatingNotFoundException;
import com.ureca.filmeet.domain.movie.exception.MovieUserNotFoundException;
import com.ureca.filmeet.domain.movie.repository.MovieRatingsRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.annotation.DistributedLock;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieRatingsCommandService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieRatingsRepository movieRatingsRepository;
    private final GenreScoreRepository genreScoreRepository;

    @DistributedLock(key = "'evaluateMovie:' + #evaluateMovieRatingRequest.movieId")
    public void evaluateMovieRating(EvaluateMovieRatingRequest evaluateMovieRatingRequest, Long userId) {
        boolean isAlreadyRating = movieRatingsRepository.existsByMovieIdAndUserId(evaluateMovieRatingRequest.movieId(),
                userId);
        if (isAlreadyRating) {
            modifyMovieRating(evaluateMovieRatingRequest, userId);
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(MovieUserNotFoundException::new);
        Movie movie = movieRepository.findMovieWithGenreByMovieId(evaluateMovieRatingRequest.movieId())
                .orElseThrow(MovieNotFoundException::new);
        BigDecimal ratingScore = evaluateMovieRatingRequest.ratingScore();
        MovieRatings movieRatings = MovieRatings.builder()
                .user(user)
                .movie(movie)
                .ratingScore(ratingScore)
                .build();
        movieRatingsRepository.save(movieRatings);

        movie.evaluateMovieRating(ratingScore);

        updateGenreScoresForUser(
                userId,
                movie,
                GenreScoreAction.RATING,
                ratingScore
        );
    }

    private void modifyMovieRating(EvaluateMovieRatingRequest modifyRatingRequest, Long userId) {
        MovieRatings movieRatings = movieRatingsRepository.findMovieRatingBy(
                        modifyRatingRequest.movieId(),
                        userId
                )
                .orElseThrow(MovieRatingNotFoundException::new);
        BigDecimal oldRatingScore = movieRatings.getRatingScore();
        BigDecimal newRatingScore = modifyRatingRequest.ratingScore();
        movieRatings.modifyRatingScore(newRatingScore);

        Movie movie = movieRepository.findMovieWithGenreByMovieId(modifyRatingRequest.movieId())
                .orElseThrow(MovieNotFoundException::new);
        movie.modifyMovieRating(oldRatingScore, newRatingScore);

        updateGenreScoresForUser(
                userId,
                movie,
                GenreScoreAction.RATING_UPDATE,
                newRatingScore.subtract(oldRatingScore)
        );
    }

    @Transactional
    public ModifyMovieRatingResponse modifyMovieRating(ModifyMovieRatingRequest modifyMovieRatingRequest) {
        MovieRatings movieRatings = movieRatingsRepository.findMovieRatingBy(
                        modifyMovieRatingRequest.movieId(),
                        modifyMovieRatingRequest.userId()
                )
                .orElseThrow(MovieRatingNotFoundException::new);
        BigDecimal oldRatingScore = movieRatings.getRatingScore();
        BigDecimal newRatingScore = modifyMovieRatingRequest.newRatingScore();
        movieRatings.modifyRatingScore(newRatingScore);

        Movie movie = movieRepository.findMovieWithGenreByMovieId(modifyMovieRatingRequest.movieId())
                .orElseThrow(MovieNotFoundException::new);
        movie.modifyMovieRating(oldRatingScore, newRatingScore);

        updateGenreScoresForUser(
                modifyMovieRatingRequest.userId(),
                movie,
                GenreScoreAction.RATING_UPDATE,
                newRatingScore.subtract(oldRatingScore)
        );

        return ModifyMovieRatingResponse.of(movieRatings.getId());
    }

    @DistributedLock(key = "'evaluateMovie:' + #deleteMovieRatingRequest.movieId")
    public void deleteMovieRating(DeleteMovieRatingRequest deleteMovieRatingRequest, Long userId) {
        MovieRatings movieRatings = movieRatingsRepository.findMovieRatingBy(
                        deleteMovieRatingRequest.movieId(),
                        userId
                )
                .orElseThrow(MovieRatingNotFoundException::new);
        BigDecimal ratingScoreToDelete = movieRatings.getRatingScore();
        movieRatingsRepository.delete(movieRatings);

        Movie movie = movieRepository.findMovieWithGenreByMovieId(deleteMovieRatingRequest.movieId())
                .orElseThrow(MovieNotFoundException::new);

        updateGenreScoresForUser(
                userId,
                movie,
                GenreScoreAction.RATING_DELETE,
                ratingScoreToDelete
        );

        // 영화의 총평점과 평균 갱신
        movie.updateAfterRatingDeletion(ratingScoreToDelete);
    }

    private void updateGenreScoresForUser(Long userId, Movie movie, GenreScoreAction genreScoreAction,
                                          BigDecimal ratingScore) {
        List<Long> genreIds = Optional.ofNullable(movie.getMovieGenres())
                .orElse(Collections.emptyList())
                .stream()
                .map(movieGenre -> movieGenre.getGenre().getId())
                .toList();

        int weightedScore = ratingScore
                .multiply(BigDecimal.valueOf(genreScoreAction.getWeight()))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();

        genreScoreRepository.bulkUpdateGenreScores(
                weightedScore,
                genreIds,
                userId
        );
    }
}
