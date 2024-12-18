package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.movie.dto.response.UpcomingMoviesResponse;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieUpcomingQueryService {

    private final MovieRepository movieRepository;

    public Slice<UpcomingMoviesResponse> getUpcomingMovies(int page, int size, LocalDate currentDate) {
        Pageable pageable = PageRequest.of(page, size, Direction.ASC, "releaseDate");
        return movieRepository.findUpcomingMoviesByDateRange(currentDate, pageable)
                .map(UpcomingMoviesResponse::of);
    }
}
