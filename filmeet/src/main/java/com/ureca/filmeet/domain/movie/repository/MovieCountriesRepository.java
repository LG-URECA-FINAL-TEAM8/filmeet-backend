package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.MovieCountries;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieCountriesRepository extends JpaRepository<MovieCountries, Long> {

    @Query("SELECT mc FROM MovieCountries mc " +
            "JOIN FETCH mc.countries c " +
            "WHERE mc.movie.id = :movieId")
    List<MovieCountries> findMovieCountriesByMovieId(@Param("movieId") Long movieId);
}
