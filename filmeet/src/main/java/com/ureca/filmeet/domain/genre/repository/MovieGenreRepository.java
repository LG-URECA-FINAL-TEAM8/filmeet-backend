package com.ureca.filmeet.domain.genre.repository;

import com.ureca.filmeet.domain.genre.entity.Genre;
import com.ureca.filmeet.domain.genre.entity.MovieGenre;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, Genre> {

    @Query("SELECT mg FROM MovieGenre mg " +
            "JOIN FETCH mg.genre g " +
            "WHERE mg.movie.id = :movieId")
    List<MovieGenre> findMovieGenresByMovieId(@Param("movieId") Long movieId);
}
