package com.ureca.filmeet.domain.review.repository;

import com.ureca.filmeet.domain.review.entity.ReviewLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikesRepository extends JpaRepository<ReviewLikes, Long> {

    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);
}
