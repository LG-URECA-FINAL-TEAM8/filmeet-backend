package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.MovieCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieCountriesRepository extends JpaRepository<MovieCountry, Long> {

    @Query("SELECT mc FROM MovieCountry mc " +
            "JOIN FETCH mc.country c " +
            "WHERE mc.movie.id = :movieId")
    List<MovieCountry> findMovieCountriesByMovieId(@Param("movieId") Long movieId);
}
