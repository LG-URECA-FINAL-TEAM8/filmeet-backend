package com.ureca.filmeet.domain.movie.service.command;

import com.ureca.filmeet.domain.genre.entity.enums.GenreScoreAction;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.dto.request.DeleteMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.EvaluateMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.ModifyMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.response.EvaluateMovieRatingResponse;
import com.ureca.filmeet.domain.movie.dto.response.ModifyMovieRatingResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRatingsRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieRatingsCommandService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieRatingsRepository movieRatingsRepository;
    private final GenreScoreRepository genreScoreRepository;

    public EvaluateMovieRatingResponse evaluateMovieRating(EvaluateMovieRatingRequest evaluateMovieRatingRequest) {
        boolean isRatingExist = movieRatingsRepository.findMovieRatingBy(
                evaluateMovieRatingRequest.movieId(),
                evaluateMovieRatingRequest.userId()
        ).isPresent();

        if (isRatingExist) {
            throw new IllegalArgumentException("생성된 평가는 또 생성할 수 없습니다.");
        }

        User user = userRepository.findById(evaluateMovieRatingRequest.userId())
                .orElseThrow(() -> new RuntimeException("no user"));
        Movie movie = movieRepository.findMovieWithGenreByMovieId(evaluateMovieRatingRequest.movieId())
                .orElseThrow(() -> new RuntimeException("no movie"));
        BigDecimal ratingScore = evaluateMovieRatingRequest.ratingScore();
        MovieRatings movieRatings = MovieRatings.builder()
                .user(user)
                .movie(movie)
                .ratingScore(ratingScore)
                .build();
        MovieRatings savedMovieRatings = movieRatingsRepository.save(movieRatings);

        movie.evaluateMovieRating(ratingScore);

        updateGenreScoresForUser(
                evaluateMovieRatingRequest.userId(),
                movie,
                GenreScoreAction.RATING,
                ratingScore
        );

        return EvaluateMovieRatingResponse.of(savedMovieRatings);
    }

    public ModifyMovieRatingResponse modifyMovieRating(ModifyMovieRatingRequest modifyMovieRatingRequest) {
        MovieRatings movieRatings = movieRatingsRepository.findById(modifyMovieRatingRequest.movieRatingId())
                .orElseThrow(() -> new RuntimeException("평가가 없습니다."));
        BigDecimal oldRatingScore = movieRatings.getRatingScore();
        BigDecimal newRatingScore = modifyMovieRatingRequest.newRatingScore();
        movieRatings.modifyRatingScore(newRatingScore);

        Movie movie = movieRepository.findMovieWithGenreByMovieId(modifyMovieRatingRequest.movieId())
                .orElseThrow(() -> new RuntimeException("no movie"));
        movie.modifyMovieRating(oldRatingScore, newRatingScore);

        updateGenreScoresForUser(
                modifyMovieRatingRequest.userId(),
                movie,
                GenreScoreAction.RATING_UPDATE,
                newRatingScore.subtract(oldRatingScore)
        );

        return ModifyMovieRatingResponse.of(movieRatings.getId());
    }

    public void deleteMovieRating(DeleteMovieRatingRequest deleteMovieRatingRequest) {
        MovieRatings movieRatings = movieRatingsRepository.findById(deleteMovieRatingRequest.movieRatingId())
                .orElseThrow(() -> new RuntimeException("삭제할 평가가 없습니다."));
        BigDecimal ratingScoreToDelete = movieRatings.getRatingScore();
        movieRatingsRepository.delete(movieRatings);

        Movie movie = movieRepository.findMovieWithGenreByMovieId(deleteMovieRatingRequest.movieId())
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다."));

        updateGenreScoresForUser(
                deleteMovieRatingRequest.userId(),
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
