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

    public List<RecommendationMoviesResponse> getMoviesRecommendation(Long memberId) {
        List<Long> genreIds = genreScoreRepository.findTop10GenreIdsByMemberId(memberId);
        List<Movie> preferredMovies = movieRepository.findMoviesByPreferredGenresAndNotInteracted(genreIds, memberId);
        Map<Movie, Double> movieScores = movieScoreService.calculateMovieScores(preferredMovies);
        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> RecommendationMoviesResponse.of(entry.getKey()))
                .collect(Collectors.toList());
    }
}