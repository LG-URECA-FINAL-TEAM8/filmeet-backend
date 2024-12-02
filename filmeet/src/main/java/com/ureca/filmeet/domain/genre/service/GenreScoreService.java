package com.ureca.filmeet.domain.genre.service;

import com.ureca.filmeet.domain.game.entity.GameResult;
import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.GenreScore;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreScoreService {

    private final GenreScoreRepository genreScoreRepository;

    @Transactional
    public void updateScoresFromGameResult(GameResult result) {
        User user = result.getUser();
        Movie movie = result.getMovie();
        int scoreChange = calculateScoreChange(result.getRank());

        // 영화의 모든 장르에 대해 점수 업데이트
        for (MovieGenre movieGenre : movie.getMovieGenres()) {
            Genre genre = movieGenre.getGenre();

            GenreScore genreScore = genreScoreRepository
                    .findByUserAndGenre(user, genre)
                    .orElseGet(() -> createNewGenreScore(user, genre));

            genreScore.addScore(scoreChange);
            genreScoreRepository.save(genreScore);
        }
    }

    private int calculateScoreChange(int rank) {
        return switch (rank) {
            case 1 -> 10;  // 우승
            case 2 -> 7;   // 준우승
            case 3 -> 5;   // 4강
            case 5 -> 3;   // 8강
            case 9 -> 1;   // 16강
            default -> 0;
        };
    }

    private GenreScore createNewGenreScore(User user, Genre genre) {
        return GenreScore.builder()
                .user(user)
                .genre(genre)
                .score(0)
                .build();
    }

    public List<Movie> getWeightedMovieSelection(User user, List<Movie> candidates, int count) {
        // 사용자의 장르 선호도 조회
        Map<Genre, Integer> userPreferences = genreScoreRepository.findByUser(user).stream()
                .collect(Collectors.toMap(
                        GenreScore::getGenre,
                        GenreScore::getScore
                ));

        // 영화별 가중치 계산
        Map<Movie, Double> movieWeights = new HashMap<>();

        for (Movie movie : candidates) {
            double weight = calculateMovieWeight(movie, userPreferences);
            movieWeights.put(movie, weight);
        }

        // 가중치 기반으로 영화 선택
        return movieWeights.entrySet().stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculateMovieWeight(Movie movie, Map<Genre, Integer> userPreferences) {
        if (movie.getMovieGenres().isEmpty()) {
            return 0.0;
        }

        return movie.getMovieGenres().stream()
                .mapToDouble(movieGenre ->
                        userPreferences.getOrDefault(movieGenre.getGenre(), 0)
                )
                .average()
                .orElse(0.0);
    }
}