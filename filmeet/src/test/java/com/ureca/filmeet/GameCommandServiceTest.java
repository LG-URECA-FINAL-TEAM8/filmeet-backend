package com.ureca.filmeet;

import com.amazonaws.services.kms.model.NotFoundException;
import com.ureca.filmeet.domain.game.dto.request.GameCreateRequest;
import com.ureca.filmeet.domain.game.dto.request.RoundMatchSelectionRequest;
import com.ureca.filmeet.domain.game.entity.Game;
import com.ureca.filmeet.domain.game.entity.RoundMatch;
import com.ureca.filmeet.domain.game.repository.GameRepository;
import com.ureca.filmeet.domain.game.repository.GameResultRepository;
import com.ureca.filmeet.domain.game.repository.RoundMatchRepository;
import com.ureca.filmeet.domain.game.service.command.GameCommandService;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class GameCommandServiceTest {
    @Autowired
    private GameCommandService gameCommandService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RoundMatchRepository roundMatchRepository;

    @MockBean
    private MovieRepository movieRepository;

    @SpyBean
    private GameResultRepository gameResultRepository;

    @Test
    @DisplayName("게임 생성 후 10분이 지나면 자동으로 삭제된다")
    void abandonedGameTest() {
        // given
        User user = createTestUser();
        Game game = createTestGame(user);

        // when
        // 게임의 modifiedAt을 11분 전으로 변경
        ReflectionTestUtils.setField(
                game,
                "modifiedAt",
                LocalDateTime.now().minusMinutes(11)
        );
        gameRepository.save(game);

        // then
        gameCommandService.cleanupAbandonedGames();
        assertThat(gameRepository.findById(game.getId())).isEmpty();
    }

    @Test
    @DisplayName("중단된 게임에서는 승자를 선택할 수 없다")
    void cannotSelectWinnerInAbandonedGame() {
        // given
        User user = createTestUser();
        Game game = createTestGame(user);
        RoundMatch match = game.getMatches().get(0);

        // modifiedAt을 11분 전으로 설정
        ReflectionTestUtils.setField(
                game,
                "modifiedAt",
                LocalDateTime.now().minusMinutes(11)
        );
        gameRepository.save(game);

        // when & then
        assertThrows(
                NotFoundException.class,
                () -> gameCommandService.selectWinner(
                        match.getId(),
                        new RoundMatchSelectionRequest(match.getMovie1().getId()),
                        user
                )
        );
    }

    @Test
    @DisplayName("동시에 여러 사용자가 같은 매치의 승자를 선택할 수 없다")
    void concurrentWinnerSelection() throws InterruptedException {
        // given
        User user = createTestUser();
        Game game = createTestGame(user);
        RoundMatch match = game.getMatches().get(0);
        int numberOfThreads = 5;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    gameCommandService.selectWinner(
                            match.getId(),
                            new RoundMatchSelectionRequest(match.getMovie1().getId()),
                            user
                    );
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 예외 발생 예상
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);

        // then
        assertThat(successCount.get()).isEqualTo(1); // 한 번만 성공해야 함
    }

    private User createTestUser() {
        return User.builder()
                .username("test")
                .password("test")
                .build();
    }

    private Game createTestGame(User user) {
        List<Movie> movies = createTestMovies(16);
        when(movieRepository.findRandomMovies(16))
                .thenReturn(movies);

        Long gameId = gameCommandService.createGame(
                new GameCreateRequest("Test Game", 16),
                user
        );

        return gameRepository.findById(gameId).orElseThrow();
    }

    private List<Movie> createTestMovies(int count) {
        List<Movie> movies = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            movies.add(Movie.builder()
                    .title("Movie " + i)
                    .releaseDate(LocalDate.now())
                    .runtime(120)
                    .build());
        }
        return movies;
    }
}
