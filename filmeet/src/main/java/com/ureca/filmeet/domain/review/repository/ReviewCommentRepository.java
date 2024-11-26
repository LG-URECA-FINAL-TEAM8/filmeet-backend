package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.review.entity.ReviewComment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {

    @Query("SELECT rc " +
            "FROM ReviewComment rc " +
            "WHERE rc.id = :reviewCommentId AND rc.isDeleted = false ")
    Optional<ReviewComment> findReviewCommentBy(@Param("reviewCommentId") Long reviewCommentId);
}
