package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.genre.entity.enums.GenreType;
import com.ureca.filmeet.domain.movie.dto.response.MovieSearchByTitleResponse;
import com.ureca.filmeet.domain.movie.dto.response.MoviesSearchByGenreResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MovieCustomRepository {

    Page<MoviesSearchByGenreResponse> searchMoviesByGenre(List<GenreType> genreTypes, Pageable pageable);

    Slice<MovieSearchByTitleResponse> searchMoviesByTitle(String title, Pageable pageable);
}
