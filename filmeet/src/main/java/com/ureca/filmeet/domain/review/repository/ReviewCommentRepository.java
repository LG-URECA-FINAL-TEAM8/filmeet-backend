package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.review.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}
