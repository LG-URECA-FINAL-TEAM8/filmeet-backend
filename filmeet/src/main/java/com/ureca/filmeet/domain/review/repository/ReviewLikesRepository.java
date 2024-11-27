package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.review.entity.ReviewLikes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikesRepository extends JpaRepository<ReviewLikes, Long> {

    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    @Query("SELECT rl " +
            "FROM ReviewLikes rl " +
            "WHERE rl.review.id = :reviewId AND rl.user.id = :userId")
    Optional<ReviewLikes> findReviewLikesByReviewIdAndUserId(
            @Param("reviewId") Long reviewId,
            @Param("userId") Long userId
    );
}
