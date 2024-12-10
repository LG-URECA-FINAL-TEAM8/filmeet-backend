package com.ureca.filmeet.domain.movie.service.query;

import static java.util.stream.Collectors.toList;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoviesRankingsRedisQueryService {

    private static final String MOVIE_RANKINGS = "MOVIE:RANKINGS";

    private final RedisTemplate<String, Object> redisTemplate;
    private final MovieRankingsQueryService movieRankingsQueryService;

    @Scheduled(cron = "0 0/30 * * * *")
    public void updateMoviesRankings() {
        log.info("Scheduled Task Started: Updating Movies Rankings in Redis...");

        try {
            log.info("Fetching movies rankings from database...");
            List<MoviesRankingsResponse> moviesRankings = movieRankingsQueryService.getMoviesRankings();
            List<Map<String, String>> rankingsCacheData = toMaps(moviesRankings);
            redisTemplate.opsForValue().set(MOVIE_RANKINGS, rankingsCacheData);
            log.info("Successfully updated movies rankings in Redis.");
        } catch (Exception e) {
            log.error("Failed to update movies rankings in Redis. Error: {}", e.getMessage(), e);
        }
    }

    public List<MoviesRankingsResponse> getMoviesRankings() {
        List<Map<String, String>> moviesRankings = getMoviesRankingsFromRedis();

        if (moviesRankings == null) {
            log.info("Fetching movies rankings from DB...");
            List<MoviesRankingsResponse> findMoviesRankings = movieRankingsQueryService.getMoviesRankings();
            saveMoviesRankings(findMoviesRankings);
            log.info("Fetched movies rankings from DB");
            return findMoviesRankings;
        }

        return moviesRankings.stream()
                .map(MoviesRankingsResponse::mapToMoviesRankingsResponse)
                .collect(toList());
    }

    private void saveMoviesRankings(List<MoviesRankingsResponse> moviesRankings) {
        List<Map<String, String>> rankingsCacheData = toMaps(moviesRankings);
        redisTemplate.opsForValue().set(MOVIE_RANKINGS, rankingsCacheData);
        log.info("saveMoviesRankings : Movies rankings saved to Redis");
    }

    private List<Map<String, String>> toMaps(List<MoviesRankingsResponse> moviesRankings) {
        return moviesRankings.stream()
                .map(MoviesRankingsResponse::toMap)
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> getMoviesRankingsFromRedis() {
        return (List<Map<String, String>>) redisTemplate.opsForValue().get(MOVIE_RANKINGS);
    }
}
