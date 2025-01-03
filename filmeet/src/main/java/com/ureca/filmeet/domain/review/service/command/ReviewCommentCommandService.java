package com.ureca.filmeet.domain.review.service.command;

import com.ureca.filmeet.domain.review.dto.request.CreateCommentRequest;
import com.ureca.filmeet.domain.review.dto.request.ModifyCommentRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateCommentResponse;
import com.ureca.filmeet.domain.review.dto.response.ModifyCommentResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.entity.ReviewComment;
import com.ureca.filmeet.domain.review.exception.ReviewCommentNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewUserNotFoundException;
import com.ureca.filmeet.domain.review.repository.ReviewCommentRepository;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.annotation.DistributedLock;
import com.ureca.filmeet.global.util.string.BadWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewCommentCommandService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final BadWordService badWordService;

    @DistributedLock(key = "'reviewComment:' + #createCommentRequest.reviewId")
    public CreateCommentResponse createComment(CreateCommentRequest createCommentRequest, Long userId) {
        Review review = reviewRepository.findReviewBy(createCommentRequest.reviewId())
                .orElseThrow(ReviewNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(ReviewUserNotFoundException::new);

        ReviewComment reviewComment = ReviewComment.builder()
                .review(review)
                .user(user)
                .content(badWordService.maskText(createCommentRequest.content()))
                .build();
        ReviewComment savedReviewComment = reviewCommentRepository.save(reviewComment);

        review.addCommentCounts();

        return CreateCommentResponse.of(savedReviewComment.getId());
    }

    @Transactional
    public ModifyCommentResponse modifyComment(ModifyCommentRequest modifyCommentRequest) {
        ReviewComment reviewComment = reviewCommentRepository.findById(modifyCommentRequest.reviewCommentId())
                .orElseThrow(ReviewCommentNotFoundException::new);

        reviewComment.modifyReviewComment(badWordService.maskText(modifyCommentRequest.content()));

        return ModifyCommentResponse.of(reviewComment.getId());
    }

    public void deleteCommentV1(Long reviewId, Long commentId) {
        Review review = reviewRepository.findReviewByReviewIdAndCommentId(reviewId, commentId)
                .orElseThrow(ReviewNotFoundException::new);

        ReviewComment reviewComment = reviewCommentRepository.findReviewCommentBy(reviewId, commentId)
                .orElseThrow(ReviewCommentNotFoundException::new);

        reviewComment.delete();
        review.decrementCommentCounts();
    }

    @DistributedLock(key = "'reviewComment:' + #reviewId")
    public void deleteComment(Long reviewId, Long commentId) {
        ReviewComment reviewComment = reviewCommentRepository.findReviewCommentWithReview(reviewId, commentId)
                .orElseThrow(ReviewCommentNotFoundException::new);
        reviewComment.delete();

        Review review = reviewComment.getReview();
        review.decrementCommentCounts();
    }
}
