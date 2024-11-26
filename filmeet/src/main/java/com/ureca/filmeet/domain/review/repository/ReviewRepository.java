package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.movie.id = :movieId AND r.user.id = :userId AND r.isDeleted = false AND r.isVisible = true ")
    Optional<Review> findReviewBy(
            @Param("movieId") Long movieId,
            @Param("userId") Long userId);

    @Query("SELECT r " +
            "FROM Review r " +
            "WHERE r.id = :reviewId AND r.isDeleted = false AND r.isVisible = true ")
    Optional<Review> findReviewBy(
            @Param("reviewId") Long reviewId);

    @Query("SELECT new com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse( " +
            "       r.id, u.id, r.content, r.likeCounts, r.commentCounts, " +
            "       r.star, u.nickname, u.profileImage, " +
            "       CASE WHEN (rl IS NOT NULL) THEN TRUE ELSE FALSE END) " +
            "FROM Review r " +
            "LEFT JOIN r.user u " +
            "LEFT JOIN ReviewLikes rl ON rl.review = r AND rl.user.id = :userId " +
            "WHERE r.movie.id = :movieId AND r.isDeleted = false AND r.isVisible = true ")
    Slice<GetMovieReviewsResponse> findMovieReviewsWithLikes(
            @Param("movieId") Long movieId,
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("SELECT r " +
            "FROM Review r " +
            "JOIN FETCH r.user u " +
            "JOIN FETCH r.movie m " +
            "LEFT JOIN FETCH r.reviewComments rc " +
            "JOIN FETCH rc.user rcu " +
            "WHERE r.isDeleted = false AND r.isVisible = true AND r.id = :reviewId " +
            "ORDER BY rc.createdAt ASC")
    Optional<Review> findMovieReviewDetailBy(@Param("reviewId") Long reviewId);
}