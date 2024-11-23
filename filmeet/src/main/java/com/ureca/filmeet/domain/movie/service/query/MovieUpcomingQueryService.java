package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieUpcomingQueryService {

    private final MovieRepository movieRepository;

    public Slice<UpcomingMoviesResponse> getUpcomingMovies(int year, int month, int page, int size) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "releaseDate");

        return movieRepository.findUpcomingMoviesByDateRange(currentDate, startDate, endDate, pageable)
                .map(UpcomingMoviesResponse::of);
    }
}
