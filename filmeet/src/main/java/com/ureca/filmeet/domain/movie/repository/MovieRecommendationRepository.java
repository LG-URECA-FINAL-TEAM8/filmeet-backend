package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.MovieRecommendation;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRecommendationRepository extends JpaRepository<MovieRecommendation, Long> {

    @Query(""
            + "SELECT mr "
            + "FROM MovieRecommendation mr "
            + "JOIN FETCH mr.movie m "
            + "WHERE mr.user.id = :userId"
    )
    List<MovieRecommendation> findMovieRecommendationByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
