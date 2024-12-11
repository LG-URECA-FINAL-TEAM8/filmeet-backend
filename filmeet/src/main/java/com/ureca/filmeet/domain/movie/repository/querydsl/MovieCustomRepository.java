package com.ureca.filmeet.domain.movie.repository.querydsl;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.MovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MovieCustomRepository {

    Slice<MoviesSearchByGenreResponse> searchMoviesByGenre(List<GenreType> genreTypes, Pageable pageable);

    Slice<MovieSearchByTitleResponse> searchMoviesByTitle(String title, Pageable pageable);

    Slice<MoviesResponse> findMoviesByGenre(GenreType genreType, Pageable pageable, Long userId);
}
