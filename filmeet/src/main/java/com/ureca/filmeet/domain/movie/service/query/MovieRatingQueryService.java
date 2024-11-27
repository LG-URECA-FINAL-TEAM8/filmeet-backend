package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.movie.dto.response.MoviesRatingResponse;
import com.ureca.filmeet.domain.movie.repository.MovieRatingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieRatingQueryService {

    private final MovieRatingsRepository movieRatingsRepository;

    public Slice<MoviesRatingResponse> getMoviesWithUserRatings(Long userId, Pageable pageable) {

        return movieRatingsRepository.findMoviesWithRatingBy(userId, pageable)
                .map(MoviesRatingResponse::of);
    }
}
