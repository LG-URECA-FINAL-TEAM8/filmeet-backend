package com.ureca.filmeet.domain.movie.batch;

import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.entity.MovieRecommendation;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.movie.service.query.MovieScoreService;
import com.ureca.filmeet.domain.movie.service.query.MoviesRankingsRedisQueryService;
import com.ureca.filmeet.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieRecommendationProcessor implements ItemProcessor<User, List<MovieRecommendation>> {

    private final MovieRepository movieRepository;
    private final GenreScoreRepository genreScoreRepository;
    private final MovieScoreService movieScoreService;
    private final MoviesRankingsRedisQueryService moviesRankingsRedisQueryService;

    @Override
    public List<MovieRecommendation> process(User user) {
        List<Long> top10MovieIds = getTop10MovieIds();
        List<Long> genreIds = genreScoreRepository.findTop10GenreIdsByMemberId(user.getId());
        List<Movie> preferredMovies = movieRepository.findMoviesByPreferredGenresAndNotInteracted(
                genreIds,
                user.getId(),
                top10MovieIds,
                PageRequest.of(0, 100)
        );
        Map<Movie, Double> movieScores = movieScoreService.calculateMovieScores(preferredMovies);

        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(20)
                .map(entry -> MovieRecommendation.builder()
                        .user(user)
                        .movie(entry.getKey())
                        .build())
                .toList();
    }

    /**
     * Filmeet TOP 10 영화 ID 리스트를 반환합니다.
     *
     * @return TOP 10 영화 ID 리스트
     */
    private List<Long> getTop10MovieIds() {
        List<Long> top10MovieIds = getTop10MovieIdsFromRedis();
        if (top10MovieIds != null && top10MovieIds.size() == 10) {
            log.info("레디스에 TOP 10 영화 데이터가 있습니다. 영화 ID: {}", top10MovieIds);
            return top10MovieIds;
        }
        log.info("레디스에 TOP 10 영화 데이터가 없어 DB로부터 데이터를 가져옵니다.");
        return getTop10MovieIdsFromDbAndSaveToRedis();
    }

    private List<Long> getTop10MovieIdsFromRedis() {
        return moviesRankingsRedisQueryService.getTop10MovieIds();
    }

    private List<Long> getTop10MovieIdsFromDbAndSaveToRedis() {
        List<Movie> movies = movieRepository.findMoviesWithStarRatingAndLikesUnion();
        Map<Movie, Double> movieScores = movieScoreService.calculateMovieScores(movies);
        saveMoviesRankings(movieScores);
        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> entry.getKey().getId())
                .collect(Collectors.toList());
    }

    private void saveMoviesRankings(Map<Movie, Double> movieScores) {
        List<MoviesRankingsResponse> moviesRankingsResponse = movieScores.entrySet()
                .stream()
                .sorted(Entry.<Movie, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> MoviesRankingsResponse.of(entry.getKey()))
                .toList();
        moviesRankingsRedisQueryService.saveMoviesRankings(moviesRankingsResponse);
    }
}
