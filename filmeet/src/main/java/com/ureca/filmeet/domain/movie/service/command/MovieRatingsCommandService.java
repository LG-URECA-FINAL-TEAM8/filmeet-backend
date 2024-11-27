package com.ureca.filmeet.domain.movie.service.command;

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

        Movie movie = movieRepository.findById(evaluateMovieRatingRequest.movieId())
                .orElseThrow(() -> new RuntimeException("no movie"));

        MovieRatings movieRatings = MovieRatings.builder()
                .user(user)
                .movie(movie)
                .ratingScore(evaluateMovieRatingRequest.ratingScore())
                .build();
        MovieRatings savedMovieRatings = movieRatingsRepository.save(movieRatings);

        movie.evaluateMovieRating(evaluateMovieRatingRequest.ratingScore());

        return EvaluateMovieRatingResponse.of(savedMovieRatings);
    }

    public ModifyMovieRatingResponse modifyMovieRating(ModifyMovieRatingRequest modifyMovieRatingRequest) {
        MovieRatings movieRatings = movieRatingsRepository.findById(modifyMovieRatingRequest.movieRatingId())
                .orElseThrow(() -> new RuntimeException("평가가 없습니다."));
        movieRatings.modifyRatingScore(modifyMovieRatingRequest.newRatingScore());

        Movie movie = movieRepository.findById(modifyMovieRatingRequest.movieId())
                .orElseThrow(() -> new RuntimeException("no movie"));
        movie.modifyMovieRating(modifyMovieRatingRequest.oldRatingScore(), modifyMovieRatingRequest.newRatingScore());

        return ModifyMovieRatingResponse.of(movieRatings.getId());
    }

    public void deleteMovieRating(DeleteMovieRatingRequest deleteMovieRatingRequest) {
        MovieRatings movieRatings = movieRatingsRepository.findById(deleteMovieRatingRequest.ratingsId())
                .orElseThrow(() -> new RuntimeException("삭제할 평가가 없습니다."));
        BigDecimal ratingScoreToDelete = movieRatings.getRatingScore();
        movieRatingsRepository.delete(movieRatings);

        Movie movie = movieRepository.findById(deleteMovieRatingRequest.movieId())
                .orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다."));
        // 영화의 총평점과 평균 갱신
        movie.updateAfterRatingDeletion(ratingScoreToDelete);
    }
}
