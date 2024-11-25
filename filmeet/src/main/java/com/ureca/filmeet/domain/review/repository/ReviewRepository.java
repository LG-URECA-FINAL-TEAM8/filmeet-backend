package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.review.entity.Review;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.movie.id = :movieId AND r.user.id = :userId AND r.isDeleted = false ")
    Optional<Review> findReviewBy(
            @Param("movieId") Long movieId,
            @Param("userId") Long userId);

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.id = :reviewId AND r.isDeleted = false ")
    Optional<Review> findReviewBy(
            @Param("reviewId") Long reviewId);
}