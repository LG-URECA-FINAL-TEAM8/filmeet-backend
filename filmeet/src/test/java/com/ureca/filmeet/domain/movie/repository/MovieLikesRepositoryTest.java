package com.ureca.filmeet.domain.movie.repository;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieLikes;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.time.LocalDate;
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
class MovieLikesRepositoryTest {

    @Autowired
    private MovieLikesRepository movieLikesRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자가 특정 영화에 좋아요를 눌렀는지 확인한다.")
    void findMovieLikesBy_whenValidMovieAndUser_returnsMovieLikes() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);
        MovieLikes movieLikes = createMovieLikes(movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        movieLikesRepository.save(movieLikes);
        Optional<MovieLikes> result = movieLikesRepository.findMovieLikesBy(movie.getId(), user.getId());

        // then
        assertThat(result)
                .isPresent()
                .get()
                .extracting("movie.id", "user.id")
                .contains(
                        movie.getId(), user.getId()
                );
    }

    @Test
    @DisplayName("사용자가 특정 영화에 좋아요를 누르지 않았다면 빈 값을 반환한다.")
    void findMovieLikesBy_whenUserNotLiked_returnsEmpty() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Optional<MovieLikes> result = movieLikesRepository.findMovieLikesBy(movie.getId(), user.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("좋아요 여부 확인 - 사용자가 특정 영화에 좋아요를 누른 경우 true를 반환한다.")
    void existsByMovieIdAndUserId_whenUserLiked_returnsTrue() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);
        MovieLikes movieLikes = createMovieLikes(movie, user);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        movieLikesRepository.save(movieLikes);
        boolean exists = movieLikesRepository.existsByMovieIdAndUserId(movie.getId(), user.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("좋아요 여부 확인 - 사용자가 특정 영화에 좋아요를 누르지 않은 경우 false를 반환한다.")
    void existsByMovieIdAndUserId_whenUserNotLiked_returnsFalse() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        boolean exists = movieLikesRepository.existsByMovieIdAndUserId(movie.getId(), user.getId());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 영화와 사용자로 조회할 경우 빈 값을 반환한다.")
    void findMovieLikesBy_whenInvalidMovieAndUser_returnsEmpty() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Optional<MovieLikes> result1 = movieLikesRepository.findMovieLikesBy(movie.getId(), 999L);
        Optional<MovieLikes> result2 = movieLikesRepository.findMovieLikesBy(999L, user.getId());

        // then
        assertThat(result1).isEmpty();
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("영화에 좋아요 데이터가 없을 때 존재 여부를 확인하면 false를 반환한다.")
    void existsByMovieIdAndUserId_whenNoLikes_returnsFalse() {
        // given
        Long nonExistentMovieId = 999L;
        Long nonExistentUserId = 999L;

        // when
        boolean exists = movieLikesRepository.existsByMovieIdAndUserId(nonExistentMovieId, nonExistentUserId);

        // then
        assertThat(exists).isFalse();
    }
}