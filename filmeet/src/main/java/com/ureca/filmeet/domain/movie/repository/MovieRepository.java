package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.Movie;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m " +
            "WHERE m.releaseDate > :currentDate " +
            "AND m.releaseDate BETWEEN :startDate AND :endDate " +
            "ORDER BY m.releaseDate ASC"
    )
    List<Movie> findUpcomingMoviesByDateRange(
            @Param("currentDate") LocalDate currentDate,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT m " +
            "FROM Movie m " +
            "WHERE m.likeCounts > 0 " +
            "AND m.averageRating > 0 "
    )
    List<Movie> findMoviesWithStarRatingAndLikes();
}