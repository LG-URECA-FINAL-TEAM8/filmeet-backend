package com.ureca.filmeet.domain.review.service.command;

import com.ureca.filmeet.domain.review.dto.request.CreateCommentRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateCommentResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewComment;
import com.ureca.filmeet.domain.review.repository.ReviewCommentRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewCommentCommandService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;

    @Transactional
    public CreateCommentResponse createComment(CreateCommentRequest createCommentRequest) {
        Review review = reviewRepository.findReviewBy(createCommentRequest.reviewId())
                .orElseThrow(() -> new RuntimeException("no review"));

        User user = userRepository.findById(createCommentRequest.userId())
                .orElseThrow(() -> new RuntimeException("no user"));

        ReviewComment reviewComment = ReviewComment.builder()
                .review(review)
                .user(user)
                .content(createCommentRequest.content())
                .build();
        ReviewComment savedReviewComment = reviewCommentRepository.save(reviewComment);

        review.addCommentCounts();

        return CreateCommentResponse.of(savedReviewComment.getId());
    }
}
