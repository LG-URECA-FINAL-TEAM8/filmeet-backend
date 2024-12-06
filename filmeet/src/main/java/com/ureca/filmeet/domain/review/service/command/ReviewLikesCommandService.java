package com.ureca.filmeet.domain.review.service.command;

import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewLikes;
import com.ureca.filmeet.domain.review.exception.ReviewLikeAlreadyExistsException;
import com.ureca.filmeet.domain.review.exception.ReviewLikeNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewUserNotFoundException;
import com.ureca.filmeet.domain.review.repository.ReviewLikesRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewLikesCommandService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikesRepository reviewLikesRepository;

    @DistributedLock(key = "'reviewLikes:' + #reviewId")
    public void reviewLikes(Long reviewId, Long userId) {
        boolean isAlreadyLiked = reviewLikesRepository.existsByReviewIdAndUserId(reviewId, userId);
        if (isAlreadyLiked) {
            throw new ReviewLikeAlreadyExistsException();
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(ReviewUserNotFoundException::new);

        ReviewLikes reviewLikes = ReviewLikes.builder()
                .review(review)
                .user(user)
                .build();
        reviewLikesRepository.save(reviewLikes);

        review.addLikeCounts();
    }

    @DistributedLock(key = "'reviewLikes:' + #reviewId")
    public void reviewLikesCancel(Long reviewId, Long userId) {
        ReviewLikes reviewLikes = reviewLikesRepository.findReviewLikesByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(ReviewLikeNotFoundException::new);
        Review review = reviewLikes.getReview();
        review.decrementLikesCounts();
        reviewLikesRepository.delete(reviewLikes);
    }
}