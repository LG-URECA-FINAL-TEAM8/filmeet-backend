package com.ureca.filmeet.domain.movie.repository;

import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieRatings;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class MovieRatingsRepositoryTest {

    @Autowired
    private MovieRatingsRepository movieRatingsRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자가 특정 영화에 대해 남긴 평점을 조회한다.")
    void findMovieRatingBy_whenValidMovieAndUser_returnsMovieRating() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);
        MovieRatings movieRatings = createMovieRatings(movie, user, BigDecimal.valueOf(4.5));

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        movieRatingsRepository.save(movieRatings);
        Optional<MovieRatings> result = movieRatingsRepository.findMovieRatingBy(movie.getId(), user.getId());

        // then
        assertThat(result)
                .isPresent()
                .get()
                .extracting("movie.id", "user.id", "ratingScore")
                .contains(
                        movie.getId(), user.getId(), BigDecimal.valueOf(4.5)
                );
    }

    @Test
    @DisplayName("사용자가 특정 영화에 대해 평점을 남기지 않았다면 빈 값을 반환한다.")
    void findMovieRatingBy_whenUserNotRated_returnsEmpty() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        Optional<MovieRatings> result = movieRatingsRepository.findMovieRatingBy(movie.getId(), user.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 사용자가 평가한 모든 영화를 페이징 처리하여 조회한다.")
    void findMoviesWithRatingBy_whenValidUser_returnsPagedRatings() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie1 = createMovie("영화1", "줄거리1", LocalDate.now(), 120, "https://poster1.url", FilmRatings.ADULT);
        Movie movie2 = createMovie("영화2", "줄거리2", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        Movie movie3 = createMovie("영화3", "줄거리3", LocalDate.now(), 130, "https://poster2.url", FilmRatings.ADULT);
        MovieRatings movieRating1 = createMovieRatings(movie1, user, BigDecimal.valueOf(3.5));
        MovieRatings movieRating2 = createMovieRatings(movie2, user, BigDecimal.valueOf(4.5));

        // when
        userRepository.save(user);
        movieRepository.saveAll(List.of(movie1, movie2, movie3));
        movieRatingsRepository.saveAll(List.of(movieRating1, movieRating2));
        PageRequest pageable = PageRequest.of(0, 10);
        Slice<MovieRatings> result = movieRatingsRepository.findMoviesWithRatingBy(user.getId(), pageable);

        // then
        assertThat(result.getContent())
                .hasSize(2)
                .extracting("movie.id", "user.id", "ratingScore")
                .containsExactlyInAnyOrder(
                        tuple(movie1.getId(), user.getId(), BigDecimal.valueOf(3.5)),
                        tuple(movie2.getId(), user.getId(), BigDecimal.valueOf(4.5))
                );
    }

    @Test
    @DisplayName("사용자가 평가한 영화가 없는 경우 빈 슬라이스를 반환한다.")
    void findMoviesWithRatingBy_whenNoRatings_returnsEmptySlice() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");

        // when
        userRepository.save(user);
        PageRequest pageable = PageRequest.of(0, 10);
        Slice<MovieRatings> result = movieRatingsRepository.findMoviesWithRatingBy(user.getId(), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("특정 영화에 사용자가 평점을 남기면 true를 반환한다.")
    void existsByMovieIdAndUserId_whenUserRated_returnsTrue() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);
        MovieRatings movieRatings = createMovieRatings(movie, user, BigDecimal.valueOf(4.5));

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        movieRatingsRepository.save(movieRatings);
        boolean exists = movieRatingsRepository.existsByMovieIdAndUserId(movie.getId(), user.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("특정 영화에 사용자가 평점을 남기지 않으면 false를 반환한다.")
    void existsByMovieIdAndUserId_whenUserNotRated_returnsFalse() {
        // given
        User user = createUser("username", "password", Role.ROLE_USER, Provider.NAVER, "nickname",
                "https://example.com/profile.jpg");
        Movie movie = createMovie("영화 제목", "영화 줄거리", LocalDate.now(), 120, "https://poster.url", FilmRatings.ADULT);

        // when
        userRepository.save(user);
        movieRepository.save(movie);
        boolean exists = movieRatingsRepository.existsByMovieIdAndUserId(movie.getId(), user.getId());

        // then
        assertThat(exists).isFalse();
    }
}