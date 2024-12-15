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
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.enums.FilmRatings;
import com.ureca.filmeet.domain.movie.repository.MovieLikesRepository;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.movie.service.command.like.MovieLikeCommandService;
import com.ureca.filmeet.domain.user.entity.Provider;
import com.ureca.filmeet.domain.user.entity.Role;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
public class MoviesLikeLockTest {

    @Autowired
    private MovieLikeCommandService movieLikeCommandServiceV1;

    @Autowired
    private MovieLikeCommandService movieLikeCommandServiceV2;

    @Autowired
    private MovieLikeCommandService movieLikeCommandServiceV3;

    @Autowired
    private MovieLikeCommandService movieLikeCommandServiceV4;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieLikesRepository movieLikeRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieGenreRepository movieGenreRepository;

    @AfterEach
    void tearDown() {
        movieLikeRepository.deleteAllInBatch();
        movieGenreRepository.deleteAllInBatch();
        genreRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
    }

//    @DisplayName("좋아요_개수_분산락_적용_X_동시성_1000명_테스트")
//    @Test
//    void likeCountWithoutDistributedLockConcurrency1000Test() throws InterruptedException {
//        int numberOfThreads = 1000;
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//        CountDownLatch latch = new CountDownLatch(numberOfThreads);
//
//        for (int i = 1; i <= numberOfThreads; i++) {
//            int num = 2006 + i;
//            executorService.submit(() -> {
//                try {
//                    movieLikeCommandServiceV1.movieLikes(1L, (long) num);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        Optional<Movie> movie = movieRepository.findById(1L);
//        assertThat(movie).isPresent();
//        assertThat(movie.get().getLikeCounts()).isEqualTo(1000);
//    }
//
//    @DisplayName("좋아요_개수_분산락_적용_동시성_1000명_테스트")
//    @Test
//    void likeCountWithDistributedLockConcurrency1000Test() throws InterruptedException {
//        int numberOfThreads = 1000;
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//        CountDownLatch latch = new CountDownLatch(numberOfThreads);
//
//        for (int i = 1; i <= numberOfThreads; i++) {
//            int num = 2006 + i;
//            executorService.submit(() -> {
//                try {
//                    movieLikeCommandServiceV2.movieLikes(1L, (long) num);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        Optional<Movie> movie = movieRepository.findById(1L);
//        assertThat(movie).isPresent();
//        assertThat(movie.get().getLikeCounts()).isEqualTo(1000);
//    }

//    @DisplayName("좋아요_개수_분산락_적용_락_흐름조정_동시성_1000명_테스트")
//    @Test
//    void likeCountWithDistributedLockAdjustedFlowConcurrency1000Test() throws InterruptedException {
//        int numberOfThreads = 1000;
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//        CountDownLatch latch = new CountDownLatch(numberOfThreads);
//
//        for (int i = 1; i <= numberOfThreads; i++) {
//            int num = 2006 + i;
//            executorService.submit(() -> {
//                try {
//                    movieLikeCommandServiceV3.movieLikes(1L, (long) num);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        Optional<Movie> movie = movieRepository.findById(1L);
//        assertThat(movie).isPresent();
//        assertThat(movie.get().getLikeCounts()).isEqualTo(1000);
//    }

    @DisplayName("좋아요_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void likeCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
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
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            executorService.submit(() -> {
                try {
                    movieLikeCommandServiceV4.movieLikes(movie.getId(), userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Optional<Movie> findMovie = movieRepository.findById(movie.getId());
        assertThat(findMovie).isPresent();
        assertThat(findMovie.get().getLikeCounts()).isEqualTo(1000);
    }

    @DisplayName("좋아요_취소_개수_분산락_적용_락_흐름조정_AOP_사용_동시성_1000명_테스트")
    @Test
    void likeCancelCountWithDistributedLockAdjustedFlowUsingAOPConcurrency1000Test() throws InterruptedException {
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
        CountDownLatch likeLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch cancelLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            executorService.submit(() -> {
                try {
                    movieLikeCommandServiceV4.movieLikes(movie.getId(), userId);
                } finally {
                    likeLatch.countDown();
                }
            });
        }
        likeLatch.await();

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = users.get(i).getId();
            executorService.submit(() -> {
                try {
                    movieLikeCommandServiceV4.movieLikesCancel(movie.getId(), userId);
                } finally {
                    cancelLatch.countDown();
                }
            });
        }
        cancelLatch.await();
        executorService.shutdown();

        // then
        Optional<Movie> findMovie = movieRepository.findById(movie.getId());
        assertThat(findMovie).isPresent();
        assertThat(findMovie.get().getLikeCounts()).isEqualTo(0);
    }
}
