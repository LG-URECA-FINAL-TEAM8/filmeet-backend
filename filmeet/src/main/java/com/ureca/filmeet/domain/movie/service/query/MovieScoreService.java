package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.movie.entity.Movie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class MovieScoreService {

    /**
     * 영화 점수 계산 로직
     */
    public Map<Movie, Double> calculateMovieScores(List<Movie> movies) {
        double maxStarRating = movies.stream()
                .mapToDouble(m -> m.getAverageRating() != null ? m.getAverageRating().doubleValue() : 0.0)
                .max()
                .orElse(1.0);
        double minStarRating = movies.stream()
                .mapToDouble(m -> m.getAverageRating() != null ? m.getAverageRating().doubleValue() : 0.0)
                .min()
                .orElse(0.0);

        int maxLikeCount = movies.stream()
                .mapToInt(m -> m.getLikeCounts() != null ? m.getLikeCounts() : 0)
                .max()
                .orElse(1);
        int minLikeCount = movies.stream()
                .mapToInt(m -> m.getLikeCounts() != null ? m.getLikeCounts() : 0)
                .min()
                .orElse(0);

        Map<Movie, Double> scores = new HashMap<>();
        double starRatingWeight = 0.7;
        double likeWeight = 0.3;

        for (Movie movie : movies) {
            double ratingScore = normalize(movie.getAverageRating().doubleValue(), minStarRating, maxStarRating);
            double likeScore = normalize(movie.getLikeCounts(), minLikeCount, maxLikeCount);

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