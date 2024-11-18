package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRankingsResponse;
import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import java.time.LocalDate;
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
    private final MovieScoreService movieScoreService;

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

        // 2. 각 영화의 점수 계산
        Map<Movie, Double> movieScores = movieScoreService.calculateMovieScores(movies);

        // 3. 점수 기준으로 정렬 및 상위 10개 영화 반환
        return movieScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> MoviesRankingsResponse.of(entry.getKey()))
                .collect(Collectors.toList());
    }
}