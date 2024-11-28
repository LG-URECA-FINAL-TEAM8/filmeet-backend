package com.ureca.filmeet.domain.game.service.command;

import com.amazonaws.services.kms.model.NotFoundException;
import com.ureca.filmeet.domain.game.dto.request.GameCreateRequest;
import com.ureca.filmeet.domain.game.dto.request.RoundMatchSelectionRequest;
import com.ureca.filmeet.domain.game.entity.Game;
import com.ureca.filmeet.domain.game.entity.GameResult;
import com.ureca.filmeet.domain.game.entity.RoundMatch;
import com.ureca.filmeet.domain.game.repository.GameRepository;
import com.ureca.filmeet.domain.game.repository.GameResultRepository;
import com.ureca.filmeet.domain.game.repository.RoundMatchRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameCommandService {

    private final GameRepository gameRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final GameResultRepository gameResultRepository;
    private final RoundMatchRepository roundMatchRepository;

    @Transactional
    public Long createGame(GameCreateRequest request, User user) {

        Game game = Game.builder()
                .title(request.title())
                .totalRounds(request.totalRounds())
                .build();

        game = gameRepository.save(game);

        // TODO: 영화 선정 로직 작성
        List<Movie> movies = movieRepository.findRandomMovies(game.getTotalRounds());
        createInitialMatches(game, user, movies);

        return game.getId();
    }

    @Transactional
    public void selectWinner(Long matchId, RoundMatchSelectionRequest request, User user) {
        RoundMatch match = roundMatchRepository.findByIdAndUserId(matchId, user.getId())
                .orElseThrow(() -> new NotFoundException("no match"));

        Movie winner = movieRepository.findById(request.selectedMovieId())
                .orElseThrow(() -> new NotFoundException("no movie"));

        match.selectWinner(winner);

        if (isRoundComplete(match.getGame(), match.getRoundNumber())) {
            // TODO: 왜 2일 때 결승전인지 설명
            if (match.getRoundNumber() == 2) { // 결승전
                finishGame(match.getGame());
            } else {
                createNextRoundMatches(match.getGame(), match.getUser(), match.getRoundNumber());
            }
        }
    }

    private void createInitialMatches(Game game, User user, List<Movie> movies) {
        for (int i = 0; i < movies.size(); i += 2) {
            RoundMatch match = RoundMatch.builder()
                    .game(game)
                    .user(user)
                    .movie1(movies.get(i))
                    .movie2(movies.get(i + 1))
                    .roundNumber(game.getTotalRounds())
                    .build();

            game.addMatch(match);
        }
    }

    private boolean isRoundComplete(Game game, int roundNumber) {
        long completedMatches = game.getMatches().stream()
                .filter(match -> match.getRoundNumber() == roundNumber)
                .filter(RoundMatch::hasWinner)
                .count();

        return completedMatches == roundNumber / 2;
    }

    private void createNextRoundMatches(Game game, User user, int currentRound) {
        int nextRound = currentRound / 2;
        List<Movie> winners = game.getMatches().stream()
                .filter((m -> m.getRoundNumber() == currentRound))
                .filter(RoundMatch::hasWinner)
                .map(m -> m.getWinner())
                .collect(Collectors.toList());

        for (int i = 0; i < winners.size(); i += 2) {
            RoundMatch match = RoundMatch.builder()
                    .game(game)
                    .user(user)
                    .movie1(winners.get(i))
                    .movie2(winners.get(i + 1))
                    .roundNumber(nextRound)
                    .build();

            game.addMatch(match);
        }
    }

    private void finishGame(Game game) {
        game.end();
        saveGameResults(game);
    }

    private void saveGameResults(Game game) {
        List<RoundMatch> matches = game.getMatches();
        Map<Long, Integer> movieRankMap = calculateMovieRanks(matches);

        movieRankMap.forEach((movieId, rank) -> {
            GameResult result = GameResult.builder()
                    .game(game)
                    .user(game.getMatches().get(0).getUser())  // 한 게임의 유저는 동일
                    .movie(movieRepository.findById(movieId)
                            .orElseThrow(() -> new NotFoundException("no movie")))
                    .rank(rank)
                    .build();

            gameResultRepository.save(result);
        });
    }

    private Map<Long, Integer> calculateMovieRanks(List<RoundMatch> matches) {
        Map<Long, Integer> movieRanks = new HashMap<>();

        // 결승전 승자 (1위)
        matches.stream()
                .filter(m -> m.getRoundNumber() == 2)  // 결승전
                .filter(RoundMatch::hasWinner)
                .findFirst()
                .ifPresent(finalMatch -> {
                    movieRanks.put(finalMatch.getWinner().getId(), 1);  // 우승

                    // 결승전 패자는 2위
                    Long loserId = finalMatch.getMovie1().getId().equals(finalMatch.getWinner().getId())
                            ? finalMatch.getMovie2().getId()
                            : finalMatch.getMovie1().getId();
                    movieRanks.put(loserId, 2);
                });

        // 4강전 패자들 (공동 3위)
        matches.stream()
                .filter(m -> m.getRoundNumber() == 4)  // 4강전
                .filter(RoundMatch::hasWinner)
                .forEach(match -> {
                    Long loserId = match.getMovie1().getId().equals(match.getWinner().getId())
                            ? match.getMovie2().getId()
                            : match.getMovie1().getId();
                    movieRanks.put(loserId, 3);
                });

        // 8강전 패자들 (공동 5위)
        matches.stream()
                .filter(m -> m.getRoundNumber() == 8)
                .filter(RoundMatch::hasWinner)
                .forEach(match -> {
                    Long loserId = match.getMovie1().getId().equals(match.getWinner().getId())
                            ? match.getMovie2().getId()
                            : match.getMovie1().getId();
                    movieRanks.put(loserId, 5);
                });

        // 16강전 패자들 (공동 9위)
        matches.stream()
                .filter(m -> m.getRoundNumber() == 16)
                .filter(RoundMatch::hasWinner)
                .forEach(match -> {
                    Long loserId = match.getMovie1().getId().equals(match.getWinner().getId())
                            ? match.getMovie2().getId()
                            : match.getMovie1().getId();
                    movieRanks.put(loserId, 9);
                });

        return movieRanks;
    }
}