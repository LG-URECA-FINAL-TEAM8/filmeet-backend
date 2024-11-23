package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.genre.repository.GenreScoreRepository;
import com.ureca.filmeet.domain.movie.dto.response.RecommendationMoviesResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieRecommendationQueryService {

    private final MovieRepository movieRepository;
    private final MovieScoreService movieScoreService;
    private final GenreScoreRepository genreScoreRepository;

    public List<RecommendationMoviesResponse> getMoviesRecommendation(Long userId, int size) {
        List<Long> top10MovieIds = getTop10MovieIds();
        List<Long> genreIds = genreScoreRepository.findTop10GenreIdsByMemberId(userId);
        List<Movie> preferredMovies = movieRepository.findMoviesByPreferredGenresAndNotInteracted(genreIds, userId,
                top10MovieIds);
        Map<Movie, Double> movieScores = movieScoreService.calculateMovieScores(preferredMovies);

        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(size)
                .map(entry -> RecommendationMoviesResponse.of(entry.getKey()))
                .collect(Collectors.toList());
    }

    /**
     * Filmeet TOP 10 영화 ID 리스트를 반환합니다.
     *
     * @return TOP 10 영화 ID 리스트
     */
    private List<Long> getTop10MovieIds() {
        List<Movie> movies = movieRepository.findMoviesWithStarRatingAndLikesUnion();
        Map<Movie, Double> movieScores = movieScoreService.calculateMovieScores(movies);

        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> entry.getKey().getId())
                .collect(Collectors.toList());
    }
}