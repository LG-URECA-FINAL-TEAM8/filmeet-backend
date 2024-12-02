package com.ureca.filmeet.domain.movie.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createGenre;
import static com.ureca.filmeet.global.util.TestUtils.createGenreScore;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieGenre;
import static com.ureca.filmeet.global.util.TestUtils.createMovieLikes;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.entity.enums.GenreScoreAction;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.exception.MovieLikeAlreadyExistsException;
import com.ureca.filmeet.domain.movie.exception.MovieLikeNotFoundException;
import com.ureca.filmeet.domain.movie.exception.MovieNotFoundException;
import com.ureca.filmeet.domain.movie.exception.MovieUserNotFoundException;
import com.ureca.filmeet.domain.movie.repository.MovieLikesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class MovieLikesCommandServiceTest {

    @Autowired
    private MovieLikesCommandService movieLikesCommandService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @Autowired
    private MovieLikesRepository movieLikesRepository;

    @Autowired
    private GenreScoreRepository genreScoreRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("사용자가 영화를 좋아요하면 좋아요가 성공적으로 저장되고 장르 점수가 업데이트된다.")
    void movieLikes_whenNotAlreadyLiked_shouldSaveLikeAndUpdateScores() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "profile.url");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.ADVENTURE);
        Genre genre4 = createGenre(GenreType.COMEDY);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre1);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre2);
        GenreScore genreScore1 = createGenreScore(user, genre1, 0);
        GenreScore genreScore2 = createGenreScore(user, genre2, 0);
        GenreScore genreScore3 = createGenreScore(user, genre3, 0);
        GenreScore genreScore4 = createGenreScore(user, genre4, 0);

        // when
        userRepository.save(user);
        movieRepository.saveAll(List.of(movie1, movie2));
        genreRepository.saveAll(List.of(genre1, genre2, genre3, genre4));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2));
        genreScoreRepository.saveAll(List.of(genreScore1, genreScore2, genreScore3, genreScore4));
        em.flush();
        em.clear();
        movieLikesCommandService.movieLikes(movie1.getId(), user.getId());
        movieLikesCommandService.movieLikes(movie2.getId(), user.getId());
        boolean isLiked1 = movieLikesRepository.existsByMovieIdAndUserId(movie1.getId(), user.getId());
        boolean isLiked2 = movieLikesRepository.existsByMovieIdAndUserId(movie2.getId(), user.getId());
        List<GenreScore> genreScores = genreScoreRepository.findAll();

        // then
        assertThat(genreScores)
                .hasSize(4)
                .extracting("user.id", "genre.id", "score")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), genre1.getId(), GenreScoreAction.LIKE.getWeight()),
                        tuple(user.getId(), genre2.getId(), GenreScoreAction.LIKE.getWeight()),
                        tuple(user.getId(), genre3.getId(), 0),
                        tuple(user.getId(), genre4.getId(), 0)
                );
        assertThat(isLiked1).isTrue();
        assertThat(isLiked2).isTrue();
    }

    @Test
    @DisplayName("이미 좋아요한 영화에 대해 좋아요를 시도하면 MovieLikeAlreadyExistsException 예외가 발생한다.")
    void movieLikes_whenAlreadyLiked_shouldThrowException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "profile.url");
        Movie movie = createMovie("Test Movie", "Plot", LocalDate.now(), 120, "poster.url", FilmRatings.ALL);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        movieLikesRepository.save(createMovieLikes(movie, user));

        // then
        assertThatThrownBy(() -> movieLikesCommandService.movieLikes(movie.getId(), user.getId()))
                .isInstanceOf(MovieLikeAlreadyExistsException.class);
    }

    @Test
    @DisplayName("좋아요를 취소하면 좋아요가 삭제되고 장르 점수가 감소한다.")
    void movieLikesCancel_whenLiked_shouldRemoveLikeAndUpdateScores() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "profile.url");
        Movie movie1 = createMovie("제목1", "줄거리1", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Movie movie2 = createMovie("제목2", "줄거리2", LocalDate.now(), 150, "https://abc", FilmRatings.ADULT);
        Genre genre1 = createGenre(GenreType.ACTION);
        Genre genre2 = createGenre(GenreType.SF);
        Genre genre3 = createGenre(GenreType.ADVENTURE);
        Genre genre4 = createGenre(GenreType.COMEDY);
        MovieLikes movieLikes1 = createMovieLikes(movie1, user);
        MovieLikes movieLikes2 = createMovieLikes(movie2, user);
        MovieGenre movieGenre1 = createMovieGenre(movie1, genre3);
        MovieGenre movieGenre2 = createMovieGenre(movie2, genre4);
        GenreScore genreScore1 = createGenreScore(user, genre1, 5);
        GenreScore genreScore2 = createGenreScore(user, genre2, 5);
        GenreScore genreScore3 = createGenreScore(user, genre3, 5);
        GenreScore genreScore4 = createGenreScore(user, genre4, 5);

        // when
        userRepository.save(user);
        movieRepository.saveAll(List.of(movie1, movie2));
        genreRepository.saveAll(List.of(genre1, genre2, genre3, genre4));
        movieLikesRepository.saveAll(List.of(movieLikes1, movieLikes2));
        movieGenreRepository.saveAll(List.of(movieGenre1, movieGenre2));
        genreScoreRepository.saveAll(List.of(genreScore1, genreScore2, genreScore3, genreScore4));
        em.flush();
        em.clear();
        movieLikesCommandService.movieLikesCancel(movie1.getId(), user.getId());
        movieLikesCommandService.movieLikesCancel(movie2.getId(), user.getId());
        boolean isLiked1 = movieLikesRepository.existsByMovieIdAndUserId(movie1.getId(), user.getId());
        boolean isLiked2 = movieLikesRepository.existsByMovieIdAndUserId(movie2.getId(), user.getId());
        List<GenreScore> genreScores = genreScoreRepository.findAll();

        // then
        assertThat(genreScores)
                .hasSize(4)
                .extracting("user.id", "genre.id", "score")
                .containsExactlyInAnyOrder(
                        tuple(user.getId(), genre1.getId(), 5),
                        tuple(user.getId(), genre2.getId(), 5),
                        tuple(user.getId(), genre3.getId(), 3),
                        tuple(user.getId(), genre4.getId(), 3)
                );
        assertThat(isLiked1).isFalse();
        assertThat(isLiked2).isFalse();
    }

    @Test
    @DisplayName("좋아요를 취소하려고 하지만 좋아요가 존재하지 않으면 MovieLikeNotFoundException 예외가 발생한다.")
    void movieLikesCancel_whenNotLiked_shouldThrowException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "profile.url");
        Movie movie = createMovie("Test Movie", "Plot", LocalDate.now(), 120, "poster.url", FilmRatings.ALL);
        Genre genre = createGenre(GenreType.ACTION);
        MovieGenre movieGenre = createMovieGenre(movie, genre);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        genreRepository.save(genre);
        movieGenreRepository.save(movieGenre);

        // then
        assertThatThrownBy(() -> movieLikesCommandService.movieLikesCancel(movie.getId(), user.getId()))
                .isInstanceOf(MovieLikeNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 영화에 대해 좋아요를 시도하면 MovieNotFoundException 예외가 발생한다.")
    void movieLikes_whenMovieNotFound_shouldThrowException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "profile.url");

        // when
        userRepository.save(user);

        // then
        assertThatThrownBy(() -> movieLikesCommandService.movieLikes(999L, user.getId()))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 좋아요를 시도하면 MovieUserNotFoundException 예외가 발생한다.")
    void movieLikes_whenUserNotFound_shouldThrowException() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "profile.url");
        Movie movie = createMovie("Test Movie", "Plot", LocalDate.now(), 120, "poster.url", FilmRatings.ALL);
        Genre genre = createGenre(GenreType.ACTION);
        MovieGenre movieGenre = createMovieGenre(movie, genre);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        genreRepository.save(genre);
        movieGenreRepository.save(movieGenre);

        // then
        assertThatThrownBy(() -> movieLikesCommandService.movieLikes(movie.getId(), 999L))
                .isInstanceOf(MovieUserNotFoundException.class);
    }

}