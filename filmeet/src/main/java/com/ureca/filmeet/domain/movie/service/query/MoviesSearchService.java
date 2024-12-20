package com.ureca.filmeet.domain.movie.service.query;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.MovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MoviesSearchService {

    private final MovieRepository movieRepository;

    public Slice<MoviesSearchByGenreResponse> searchMoviesByGenre(List<GenreType> genreTypes, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.searchMoviesByGenre(genreTypes, pageable);
    }

    public Slice<MovieSearchByTitleResponse> searchMoviesByTitleV1(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.searchMoviesByTitle(title, pageable);
    }

    public Slice<MovieSearchByTitleResponse> searchMoviesByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findMoviesByTitle(title, pageable)
                .map(MovieSearchByTitleResponse::of);
    }
}
