package com.ureca.filmeet.domain.movie.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createGenre;
import static com.ureca.filmeet.global.util.TestUtils.createGenreScore;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieGenre;
import static com.ureca.filmeet.global.util.TestUtils.createMovieRatings;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.dto.request.DeleteMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.EvaluateMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.ModifyMovieRatingRequest;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.exception.MovieRatingAlreadyExistsException;
import com.ureca.filmeet.domain.movie.repository.MovieRatingsRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class MovieRatingsCommandServiceTest {

    @Autowired
    private MovieRatingsCommandService movieRatingsCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieRatingsRepository movieRatingsRepository;

    @Autowired
    private GenreScoreRepository genreScoreRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("영화에 대해 새로운 평점을 남기고 해당 영화의 장르에 따라 유저의 장르 점수가 업데이트된다..")
    void evaluateMovieRating_whenValidRequest_savesRatingAndUpdatesScores() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "profile.url");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie3 = createMovie("제목3", "줄거리3", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.ADVENTURE);
        Genre genre4 = createGenre(GenreType.COMEDY);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre1);
        MovieGenre movieGenre3 = createMovieGenre(movie2, genre2);
        MovieGenre movieGenre4 = createMovieGenre(movie3, genre3);
        GenreScore genreScore1 = createGenreScore(user, genre1, 0);
        GenreScore genreScore2 = createGenreScore(user, genre2, 0);
        GenreScore genreScore3 = createGenreScore(user, genre3, 0);
        GenreScore genreScore4 = createGenreScore(user, genre4, 0);

        // when
        userRepository.save(user);
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        genreRepository.saveAll(List.of(genre1, genre2, genre3, genre4));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2, movieGenre3, movieGenre4));
        genreScoreRepository.saveAll(List.of(genreScore1, genreScore2, genreScore3, genreScore4));
        EvaluateMovieRatingRequest request1 = new EvaluateMovieRatingRequest(movie1.getId(),
                BigDecimal.valueOf(4.5));
        EvaluateMovieRatingRequest request2 = new EvaluateMovieRatingRequest(movie2.getId(),
                BigDecimal.valueOf(3.5));
        EvaluateMovieRatingRequest request3 = new EvaluateMovieRatingRequest(movie3.getId(),
                BigDecimal.valueOf(2.5));
        em.flush();
        em.clear();
        movieRatingsCommandService.evaluateMovieRating(request1, user.getId());
        movieRatingsCommandService.evaluateMovieRating(request2, user.getId());
        movieRatingsCommandService.evaluateMovieRating(request3, user.getId());
        boolean isRated1 = movieRatingsRepository.existsByMovieIdAndUserId(movie1.getId(), user.getId());
        boolean isRated2 = movieRatingsRepository.existsByMovieIdAndUserId(movie2.getId(), user.getId());
        boolean isRated3 = movieRatingsRepository.existsByMovieIdAndUserId(movie3.getId(), user.getId());
        List<GenreScore> genreScores = genreScoreRepository.findAll();

        // then
        assertThat(genreScores)
                .hasSize(4)
                .extracting("user.id", "genre.id", "score")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), genre1.getId(), 9),
                        tuple(user.getId(), genre2.getId(), 4),
                        tuple(user.getId(), genre3.getId(), 3),
                        tuple(user.getId(), genre4.getId(), 0)
                );
        assertThat(isRated1).isTrue();
        assertThat(isRated2).isTrue();
        assertThat(isRated3).isTrue();
    }

    @Test
    @DisplayName("이미 평점을 남긴 영화에 대해 새로운 평점을 남기려고 하면 MovieRatingAlreadyExistsException 예외가 발생한다.")
    void evaluateMovieRating_whenAlreadyRated_throwsException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname", "profile.url");
        Movie movie = createMovie("Test Movie", "Plot", LocalDate.now(), 120, "poster.url", FilmRatings.ALL);
        MovieRatings movieRatings = createMovieRatings(movie, user, BigDecimal.valueOf(4.0));

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        movieRatingsRepository.save(movieRatings);
        EvaluateMovieRatingRequest request = new EvaluateMovieRatingRequest(movie.getId(), BigDecimal.valueOf(4.5));

        // then
        assertThatThrownBy(() -> movieRatingsCommandService.evaluateMovieRating(request, user.getId()))
                .isInstanceOf(MovieRatingAlreadyExistsException.class);
    }

    @Test
    @DisplayName("영화 평점을 수정한다.")
    void modifyMovieRating_whenValidRequest_updatesRatingAndScores() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "profile.url");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie3 = createMovie("제목3", "줄거리3", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.ADVENTURE);
        Genre genre4 = createGenre(GenreType.COMEDY);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre2);
        MovieGenre movieGenre3 = createMovieGenre(movie2, genre2);
        MovieGenre movieGenre4 = createMovieGenre(movie3, genre3);
        GenreScore genreScore1 = createGenreScore(user, genre1, 0);
        GenreScore genreScore2 = createGenreScore(user, genre2, 0);
        GenreScore genreScore3 = createGenreScore(user, genre3, 0);
        GenreScore genreScore4 = createGenreScore(user, genre4, 0);
        MovieRatings movieRatings1 = createMovieRatings(movie1, user, BigDecimal.valueOf(1.5));
        MovieRatings movieRatings2 = createMovieRatings(movie2, user, BigDecimal.valueOf(2.5));
        MovieRatings movieRatings3 = createMovieRatings(movie3, user, BigDecimal.valueOf(5.0));

        // when
        userRepository.save(user);
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        genreRepository.saveAll(List.of(genre1, genre2, genre3, genre4));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2, movieGenre3, movieGenre4));
        genreScoreRepository.saveAll(List.of(genreScore1, genreScore2, genreScore3, genreScore4));
        movieRatingsRepository.saveAll(List.of(movieRatings1, movieRatings2, movieRatings3));
        ModifyMovieRatingRequest request1 = new ModifyMovieRatingRequest(movie1.getId(), user.getId(),
                BigDecimal.valueOf(1.0));
        ModifyMovieRatingRequest request2 = new ModifyMovieRatingRequest(movie2.getId(), user.getId(),
                BigDecimal.valueOf(2.5));
        ModifyMovieRatingRequest request3 = new ModifyMovieRatingRequest(movie3.getId(), user.getId(),
                BigDecimal.valueOf(3.0));
        em.flush();
        em.clear();
        movieRatingsCommandService.modifyMovieRating(request1);
        movieRatingsCommandService.modifyMovieRating(request2);
        movieRatingsCommandService.modifyMovieRating(request3);
        Optional<MovieRatings> movieRating1 = movieRatingsRepository.findMovieRatingBy(movie1.getId(), user.getId());
        Optional<MovieRatings> movieRating2 = movieRatingsRepository.findMovieRatingBy(movie2.getId(), user.getId());
        Optional<MovieRatings> movieRating3 = movieRatingsRepository.findMovieRatingBy(movie3.getId(), user.getId());

        boolean isRated1 = movieRatingsRepository.existsByMovieIdAndUserId(movie1.getId(), user.getId());
        boolean isRated2 = movieRatingsRepository.existsByMovieIdAndUserId(movie2.getId(), user.getId());
        boolean isRated3 = movieRatingsRepository.existsByMovieIdAndUserId(movie3.getId(), user.getId());
        List<GenreScore> genreScores = genreScoreRepository.findAll();

        // then
        assertThat(isRated1).isTrue();
        assertThat(isRated2).isTrue();
        assertThat(isRated3).isTrue();
        assertThat(genreScores)
                .hasSize(4)
                .extracting("user.id", "genre.id", "score")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), genre1.getId(), -1),
                        tuple(user.getId(), genre2.getId(), 0),
                        tuple(user.getId(), genre3.getId(), -2),
                        tuple(user.getId(), genre4.getId(), 0)
                );
        assertThat(movieRating1)
                .isPresent()
                .get().extracting("movie.id", "user.id", "ratingScore")
                .containsExactly(movie1.getId(), user.getId(), BigDecimal.valueOf(1.0));
        assertThat(movieRating2)
                .isPresent()
                .get().extracting("movie.id", "user.id", "ratingScore")
                .containsExactly(movie2.getId(), user.getId(), BigDecimal.valueOf(2.5));
        assertThat(movieRating3)
                .isPresent()
                .get().extracting("movie.id", "user.id", "ratingScore")
                .containsExactly(movie3.getId(), user.getId(), BigDecimal.valueOf(3.0));
    }

    @Test
    @DisplayName("영화 평점을 삭제하면 평점 및 장르 점수가 올바르게 업데이트된다.")
    void deleteMovieRating_whenValidRequest_deletesRatingAndUpdatesScores() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname", "profile.url");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://poster1.url", FilmRatings.ALL,
                BigDecimal.valueOf(0.0), 1, 0);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.COMEDY);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie1, genre2);
        GenreScore genreScore1 = createGenreScore(user, genre1, 10);
        GenreScore genreScore2 = createGenreScore(user, genre2, 15);
        MovieRatings movieRatings = createMovieRatings(movie1, user, BigDecimal.valueOf(4.5));

        // when
        userRepository.save(user);
        movieRepository.save(movie1);
        genreRepository.saveAll(List.of(genre1, genre2));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2));
        genreScoreRepository.saveAll(List.of(genreScore1, genreScore2));
        movieRatingsRepository.save(movieRatings);

        em.flush();
        em.clear();

        DeleteMovieRatingRequest deleteRequest = new DeleteMovieRatingRequest(movie1.getId(), user.getId());
        movieRatingsCommandService.deleteMovieRating(deleteRequest);

        Optional<MovieRatings> deletedRating = movieRatingsRepository.findMovieRatingBy(movie1.getId(), user.getId());
        Movie updatedMovie = movieRepository.findById(movie1.getId()).orElseThrow();
        List<GenreScore> updatedGenreScores = genreScoreRepository.findAll();

        // then
        assertThat(deletedRating).isNotPresent();
        assertThat(updatedMovie.getAverageRating()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(updatedGenreScores)
                .hasSize(2)
                .extracting("user.id", "genre.id", "score")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), genre1.getId(), 5),
                        tuple(user.getId(), genre2.getId(), 10)
                );
    }
}