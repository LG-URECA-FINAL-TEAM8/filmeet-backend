package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.domain.movie.entity.MovieLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieLikesRepository extends JpaRepository<MovieLikes, Long> {

    @Query("SELECT ml " +
            "FROM MovieLikes ml " +
            "WHERE ml.movie.id = :movieId AND ml.user.id = :memberId")
    Optional<MovieLikes> findMovieLikesBy(
            @Param("movieId") Long movieId,
            @Param("memberId") Long memberId);

    boolean existsByMovieIdAndUserId(Long movieId, Long userId);

    List<MovieLikes> findByMovie_Id(Long movieId);
}
