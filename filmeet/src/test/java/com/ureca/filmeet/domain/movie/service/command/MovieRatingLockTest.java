package com.ureca.filmeet.domain.movie.service.command;

import static com.ureca.filmeet.global.util.TestUtils.createGenre;
import static com.ureca.filmeet.global.util.TestUtils.createMovie;
import static com.ureca.filmeet.global.util.TestUtils.createMovieGenre;
import static com.ureca.filmeet.global.util.TestUtils.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.genre.repository.GenreRepository;
import com.ureca.filmeet.domain.genre.repository.MovieGenreRepository;
import com.ureca.filmeet.domain.movie.dto.request.DeleteMovieRatingRequest;
import com.ureca.filmeet.domain.movie.dto.request.EvaluateMovieRatingRequest;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieRatingsRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("local")
public class MovieRatingLockTest {

    @Autowired
    private MovieRatingsCommandService movieRatingsCommandService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @Autowired
    private MovieRatingsRepository movieRatingRepository;

    @AfterEach
    void tearDown() {
        movieRatingRepository.deleteAllInBatch();
        movieGenreRepository.deleteAllInBatch();
        genreRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
    }

    @DisplayName("평점_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void ratingCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        // given
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        Genre genre = createGenre(GenreType.ACTION);
        MovieGenre movieGenre = createMovieGenre(movie, genre);

        movieRepository.save(movie);
        genreRepository.save(genre);
        movieGenreRepository.save(movieGenre);

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            User user = createUser("user" + i, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임" + i,
                    "https://example.com/profile" + i + ".jpg");
            users.add(user);
        }
        userRepository.saveAll(users);

        // when
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch rateLatch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            EvaluateMovieRatingRequest evaluateMovieRatingRequest = new EvaluateMovieRatingRequest(movie.getId(),
                    BigDecimal.valueOf(4.5));

            executorService.submit(() -> {
                try {
                    movieRatingsCommandService.evaluateMovieRating(evaluateMovieRatingRequest, userId);
                } finally {
                    rateLatch.countDown();
                }
            });
        }
        rateLatch.await();
        executorService.shutdown();

        // then
        Optional<Movie> findMovie = movieRepository.findById(movie.getId());
        assertThat(findMovie).isPresent();
        assertThat(findMovie.get().getRatingCounts()).isEqualTo(1000);
    }

    @DisplayName("평점_삭제_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void ratingDeleteCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
        // given
        Movie movie = createMovie("제목", "줄거리", LocalDate.now(), 150, "https://poster.jpg", FilmRatings.ADULT);
        movieRepository.save(movie);

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            User user = createUser("user" + i, "password", Role.ROLE_ADULT_USER, Provider.NAVER, "닉네임" + i,
                    "https://example.com/profile" + i + ".jpg");
            users.add(user);
        }
        userRepository.saveAll(users);

        // when
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch rateLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch deleteLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            EvaluateMovieRatingRequest evaluateMovieRatingRequest = new EvaluateMovieRatingRequest(movie.getId(),
                    BigDecimal.valueOf(4.5));

            executorService.submit(() -> {
                try {
                    movieRatingsCommandService.evaluateMovieRating(evaluateMovieRatingRequest, userId);
                } finally {
                    rateLatch.countDown();
                }
            });
        }
        rateLatch.await();

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            DeleteMovieRatingRequest deleteMovieRatingRequest = new DeleteMovieRatingRequest(movie.getId());

            executorService.submit(() -> {
                try {
                    movieRatingsCommandService.deleteMovieRating(deleteMovieRatingRequest, userId);
                } finally {
                    deleteLatch.countDown();
                }
            });
        }
        deleteLatch.await();
        executorService.shutdown();

        // then
        Optional<Movie> findMovie = movieRepository.findById(movie.getId());
        assertThat(findMovie).isPresent();
        assertThat(findMovie.get().getRatingCounts()).isEqualTo(0);
    }
}
