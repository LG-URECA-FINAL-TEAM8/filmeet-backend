package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.dto.response.RatingDistributionResponse;
import com.ureca.filmeet.domain.movie.entity.MovieRatings;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @Query("SELECT mr " +
            "FROM MovieRatings mr " +
            "JOIN FETCH mr.movie m " +
            "JOIN FETCH mr.user u " +
            "WHERE mr.user.id = :userId")
    Slice<MovieRatings> findMoviesWithRatingBy(
            @Param("userId") Long userId,
            Pageable pageable);

    boolean existsByMovieIdAndUserId(Long movieId, Long userId);

    @Query("""
                SELECT new com.ureca.filmeet.domain.movie.dto.response.RatingDistributionResponse(
                    mr.ratingScore, COUNT(mr.ratingScore)
                )
                FROM MovieRatings mr
                WHERE mr.movie.id = :movieId
                GROUP BY mr.ratingScore
                ORDER BY mr.ratingScore ASC
            """)
    List<RatingDistributionResponse> findRatingDistributionByMovieId(
            @Param("movieId") Long movieId
    );
}
