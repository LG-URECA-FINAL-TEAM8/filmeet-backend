package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.review.entity.ReviewComment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    @Query("SELECT rc " +
            "FROM ReviewComment rc " +
            "JOIN rc.review r " +
            "WHERE rc.id = :commentId " +
            "AND r.id = :reviewId " +
            "AND rc.isDeleted = false ")
    Optional<ReviewComment> findReviewCommentBy(
            @Param("reviewId") Long reviewId,
            @Param("commentId") Long commentId
    );

    @Query("SELECT rc " +
            "FROM ReviewComment rc " +
            "JOIN rc.review r " +
            "WHERE rc.id = :commentId " +
            "AND r.id = :reviewId " +
            "AND rc.isDeleted = false " +
            "AND r.isDeleted = false " +
            "AND r.isVisible = true")
    Optional<ReviewComment> findReviewCommentWithReview(
            @Param("reviewId") Long reviewId,
            @Param("commentId") Long commentId
    );
}
