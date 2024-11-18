package com.ureca.filmeet.domain.movie.service;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieQueryService {

    private final MovieRepository movieRepository;

    public List<UpcomingMoviesResponse> getUpcomingMovies(int year, int month) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return movieRepository.findUpcomingMoviesByDateRange(currentDate,
                        startDate, endDate)
                .stream()
                .map(UpcomingMoviesResponse::of)
                .toList();
    }

    /**
     * Filmeet TOP 10 영화 리스트를 반환합니다.
     *
     * @return TOP 10 영화 리스트
     */
    public List<MoviesRankingsResponse> getMoviesRankings() {
        // 1. 별점 & 좋아요 있는 영화 가져오기
        List<Movie> movies = movieRepository.findMoviesWithStarRatingAndLikes();

        // 2. 최대값 동적 계산
        double maxStarRating = movies.stream().mapToDouble(m -> m.getAverageRating().doubleValue()).max().orElse(1.0);
        double minStarRating = movies.stream().mapToDouble(m -> m.getAverageRating().doubleValue()).min().orElse(0.0);
        int maxLikeCount = movies.stream().mapToInt(Movie::getLikeCounts).max().orElse(1);
        int minLikeCount = movies.stream().mapToInt(Movie::getLikeCounts).min().orElse(0);

        // 3. 각 영화의 점수 계산
        Map<Movie, Double> movieScores = calculateMovieScores(
                movies, maxStarRating, minStarRating, maxLikeCount, minLikeCount
        );

        // 4. 점수 기준으로 정렬 및 상위 10개 영화 반환
        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> MoviesRankingsResponse.of(entry.getKey())) // Movie 객체를 MoviesRankingsResponse로 변환
                .collect(Collectors.toList());
    }

    /**
     * 영화 점수 계산 로직
     */
    private Map<Movie, Double> calculateMovieScores(
            List<Movie> movies,
            double maxStarRating, double minStarRating,
            int maxLikeCount, int minLikeCount) {

        Map<Movie, Double> scores = new HashMap<>();
        double starRatingWeight = 0.7;
        double likeWeight = 0.3;

        for (Movie movie : movies) {
            // 정규화
            double ratingScore = normalize(movie.getAverageRating().doubleValue(), minStarRating, maxStarRating);
            double likeScore = normalize(movie.getLikeCounts(), minLikeCount, maxLikeCount);

            // 총 점수 계산
            double totalScore = (ratingScore * starRatingWeight) + (likeScore * likeWeight);

            scores.put(movie, totalScore);
        }
        return scores;
    }

    /**
     * Min-Max 정규화: 0~1 사이 값으로 변환
     */
    private double normalize(double value, double minValue, double maxValue) {
        if (maxValue == minValue) {
            return 0.0;
        }
        return (value - minValue) / (maxValue - minValue);
    }
}