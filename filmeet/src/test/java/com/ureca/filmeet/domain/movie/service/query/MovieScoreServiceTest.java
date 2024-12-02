package com.ureca.filmeet.domain.movie.service.query;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class MovieScoreServiceTest {

    @Autowired
    private MovieScoreService movieScoreService;

    @Test
    @DisplayName("모든 영화의 평점과 좋아요가 동일한 경우 점수가 0으로 계산된다.")
    void calculateMovieScores_whenAllMoviesHaveSameRatingAndLikes_scoresAreZero() {
        // given
        Movie movie1 = createMovie("Movie 1", "Plot 1", LocalDate.now(), 120, "poster1.url", FilmRatings.ALL,
                BigDecimal.valueOf(3.0), 10, 10);
        Movie movie2 = createMovie("Movie 2", "Plot 2", LocalDate.now(), 100, "poster2.url", FilmRatings.ALL,
                BigDecimal.valueOf(3.0), 10, 10);
        List<Movie> movies = List.of(movie1, movie2);

        // when
        Map<Movie, Double> scores = movieScoreService.calculateMovieScores(movies);

        // then
        assertThat(scores.size()).isEqualTo(2);
        assertThat(scores.entrySet())
                .allSatisfy(entry -> assertThat(entry.getValue()).isEqualTo(0.0));
    }

    @Test
    @DisplayName("평점과 좋아요 수가 다양한 영화의 점수를 계산한다.")
    void calculateMovieScores_whenMoviesHaveDifferentRatingsAndLikes_calculatesCorrectScores() {
        // given
        Movie movie1 = createMovie("Movie 1", "Plot 1", LocalDate.now(), 120, "poster1.url", FilmRatings.ALL,
                BigDecimal.valueOf(4.0), 10, 20);
        Movie movie2 = createMovie("Movie 2", "Plot 2", LocalDate.now(), 100, "poster2.url", FilmRatings.ALL,
                BigDecimal.valueOf(2.0), 10, 5);
        Movie movie3 = createMovie("Movie 3", "Plot 3", LocalDate.now(), 150, "poster3.url", FilmRatings.ALL,
                BigDecimal.valueOf(3.5), 10, 15);
        List<Movie> movies = List.of(movie1, movie2, movie3);

        // when
        Map<Movie, Double> scores = movieScoreService.calculateMovieScores(movies);

        // then
        assertThat(scores).hasSize(3);
        assertThat(scores.get(movie1)).isGreaterThan(scores.get(movie3));
        assertThat(scores.get(movie3)).isGreaterThan(scores.get(movie2));
        assertThat(scores.get(movie2)).isLessThan(scores.get(movie1));
    }

    @Test
    @DisplayName("영화 목록이 비어있을 경우 빈 점수 맵을 반환한다.")
    void calculateMovieScores_whenNoMovies_returnsEmptyMap() {
        // given
        List<Movie> movies = List.of();

        // when
        Map<Movie, Double> scores = movieScoreService.calculateMovieScores(movies);

        // then
        assertThat(scores).isEmpty();
    }

    @Test
    @DisplayName("평점이 모두 0이고 좋아요 수가 다른 영화의 점수를 계산한다.")
    void calculateMovieScores_whenAllRatingsAreZero_calculatesBasedOnLikesOnly() {
        // given
        Movie movie1 = createMovie("Movie 1", "Plot 1", LocalDate.now(), 120, "poster1.url", FilmRatings.ALL,
                BigDecimal.valueOf(0.0), 10, 10);
        Movie movie2 = createMovie("Movie 2", "Plot 2", LocalDate.now(), 100, "poster2.url", FilmRatings.ALL,
                BigDecimal.valueOf(0.0), 10, 11);
        List<Movie> movies = List.of(movie1, movie2);

        // when
        Map<Movie, Double> scores = movieScoreService.calculateMovieScores(movies);

        // then
        assertThat(scores.get(movie1)).isLessThan(scores.get(movie2));
    }

    @Test
    @DisplayName("좋아요 수가 모두 0이고 평점이 다른 영화의 점수를 계산한다.")
    void calculateMovieScores_whenAllLikesAreZero_calculatesBasedOnRatingsOnly() {
        // given
        // given
        Movie movie1 = createMovie("Movie 1", "Plot 1", LocalDate.now(), 120, "poster1.url", FilmRatings.ALL,
                BigDecimal.valueOf(2.1), 10, 0);
        Movie movie2 = createMovie("Movie 2", "Plot 2", LocalDate.now(), 100, "poster2.url", FilmRatings.ALL,
                BigDecimal.valueOf(2.0), 10, 0);
        List<Movie> movies = List.of(movie1, movie2);

        // when
        Map<Movie, Double> scores = movieScoreService.calculateMovieScores(movies);

        // then
        assertThat(scores.get(movie1)).isGreaterThan(scores.get(movie2));
    }
}