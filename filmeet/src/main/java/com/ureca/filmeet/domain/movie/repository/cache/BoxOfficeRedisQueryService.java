package com.ureca.filmeet.domain.movie.repository.cache;

import static java.util.stream.Collectors.toList;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.infra.kobis.KobisOpenAPIRestService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoxOfficeRedisQueryService {

    private static final String MOVIE_BOX_OFFICE = "MOVIE:BOX:OFFICE";

    private final RedisTemplate<String, Object> redisTemplate;

    private final MovieRepository movieRepository;
    private final KobisOpenAPIRestService kobisOpenAPIRestService;

    @Scheduled(cron = "0 0 * * * ?")
    public void updateBoxOfficeMovies() {
        try {
            log.info("Update box office data...");
            List<Map<String, String>> boxOfficeMovies = fetchBoxOfficeMovies();
            redisTemplate.opsForValue().set(MOVIE_BOX_OFFICE, boxOfficeMovies);
            log.info("Successfully updated box office data in cache.");
        } catch (Exception e) {
            log.error("Failed to update box office data in Redis. Error: {}", e.getMessage(), e);
        }
    }

    private List<Map<String, String>> fetchBoxOfficeMovies() {
        try {
            log.info("Fetching box office data...");
            List<Map<String, String>> boxOfficeMovies = kobisOpenAPIRestService.fetchDailyBoxOffice();
            log.info("Successfully fetched box office data from API. Logging individual movie details...");
            for (Map<String, String> boxOfficeMovie : boxOfficeMovies) {
                log.info("releaseDate {} ", boxOfficeMovie.get("releaseDate"));
                log.info("movieName {} ", boxOfficeMovie.get("movieName"));
            }

            List<String> movieNames = boxOfficeMovies.stream()
                    .map(boxOfficeMovie -> boxOfficeMovie.get("movieName"))
                    .filter(movieName -> movieName != null && !movieName.isEmpty()) // Null 또는 빈 값 필터링
                    .collect(toList());

            if (movieNames.isEmpty()) {
                log.warn("No movie names found in box office data.");
                return boxOfficeMovies; // 영화 제목이 없으면 원본 반환
            }

            List<Movie> movies = movieRepository.findMoviesByTitles(movieNames);

            // 조회 결과를 Map<String, Movie>로 변환 (키: movieName)
            Map<String, Movie> movieMap = movies.stream()
                    .collect(Collectors.toMap(Movie::getTitle, movie -> movie));

            // boxOfficeMovies 순회하며 데이터 업데이트
            boxOfficeMovies.forEach(boxOfficeMovie -> {
                String movieName = boxOfficeMovie.get("movieName");
                Movie movie = movieMap.get(movieName);

                if (movie != null) {
                    boxOfficeMovie.put("movieId", String.valueOf(movie.getId()));
                    boxOfficeMovie.put("posterUrl", movie.getPosterUrl());
                } else {
                    log.warn("Movie not found in repository: {}", movieName);
                }
            });

            return boxOfficeMovies;
        } catch (RuntimeException e) {
            log.error("Error fetching box office data, retrying...", e);
            throw e;
        }
    }

    public List<Map<String, String>> getBoxOfficeMovies() {
        List<Map<String, String>> moviesBoxOffice = getMoviesBoxOfficeFromRedis();
        if (moviesBoxOffice == null) {
            log.info("Fetching movies box office from kobis ...");
            List<Map<String, String>> findMoviesBoxOffice = fetchBoxOfficeMovies();
            saveMoviesBoxOffice(findMoviesBoxOffice);
            log.info("Fetched movies box office from kobis");
            return findMoviesBoxOffice;
        }

        log.info("Fetched movies box office from Redis");
        return moviesBoxOffice;
    }

    private void saveMoviesBoxOffice(List<Map<String, String>> moviesBoxOffice) {
        redisTemplate.opsForValue().set(MOVIE_BOX_OFFICE, moviesBoxOffice);
        log.info("saveMoviesBoxOffice : Movies box office saved to Redis");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> getMoviesBoxOfficeFromRedis() {
        return (List<Map<String, String>>) redisTemplate.opsForValue().get(MOVIE_BOX_OFFICE);
    }
}