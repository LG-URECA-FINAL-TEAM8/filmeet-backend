package com.ureca.filmeet.domain.review.service.command;

import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewLikes;
import com.ureca.filmeet.domain.review.repository.ReviewLikesRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewLikesCommandService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikesRepository reviewLikesRepository;

    public void reviewLikes(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("no review"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("no user"));

        ReviewLikes reviewLikes = ReviewLikes.builder()
                .review(review)
                .user(user)
                .build();
        reviewLikesRepository.save(reviewLikes);

        review.addLikeCounts();
    }
}