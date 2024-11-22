package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieUpcomingQueryService {

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
}
