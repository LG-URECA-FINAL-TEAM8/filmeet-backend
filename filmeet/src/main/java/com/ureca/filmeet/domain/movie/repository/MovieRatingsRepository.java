package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRatingsRepository extends JpaRepository<MovieRatings, Long> {

    @Query("SELECT mr " +
            "FROM MovieRatings mr " +
            "WHERE mr.movie.id = :movieId " +
            "AND mr.user.id = :userId")
    Optional<MovieRatings> findMovieRatingBy(
            @Param("movieId") Long movieId,
            @Param("userId") Long userId
    );
}
