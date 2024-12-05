package com.ureca.filmeet.domain.review.service.command;

import com.ureca.filmeet.domain.follow.entity.Follow;
import com.ureca.filmeet.domain.follow.repository.FollowRepository;
import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.dto.request.CreateReviewRequest;
import com.ureca.filmeet.domain.review.dto.request.ModifyReviewRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateReviewResponse;
import com.ureca.filmeet.domain.review.dto.response.ModifyReviewResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.exception.ReviewAlreadyExistsException;
import com.ureca.filmeet.domain.review.exception.ReviewMovieNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewNotFoundException;
import com.ureca.filmeet.domain.review.exception.ReviewUserNotFoundException;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import com.ureca.filmeet.global.notification.service.command.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final FollowRepository followRepository;
    private final NotificationCommandService notificationCommandService;

    public CreateReviewResponse createReview(CreateReviewRequest createReviewRequest) {
        boolean isAlreadyReview = reviewRepository.existsByUserIdAndMovieIdAndIsDeletedFalseAndIsVisibleTrue(
                createReviewRequest.userId(),
                createReviewRequest.movieId());
        if (isAlreadyReview) {
            throw new ReviewAlreadyExistsException();
        }

        User user = userRepository.findById(createReviewRequest.userId())
                .orElseThrow(ReviewUserNotFoundException::new);

        Movie movie = movieRepository.findById(createReviewRequest.movieId())
                .orElseThrow(ReviewMovieNotFoundException::new);

        Review review = Review.builder()
                .content(createReviewRequest.content())
                .movie(movie)
                .user(user)
                .build();

        Review saveReview = reviewRepository.save(review);
        // 댓글 작성자의 팔로워들 조회
        List<User> followers = followRepository.findAllByFollowing(user)
                .stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());

        // 팔로워들에게 알림 발송
        notificationCommandService.sendReviewNotification(user, review.getId(), followers);

        return CreateReviewResponse.of(saveReview.getId());
    }

    public ModifyReviewResponse modifyReview(ModifyReviewRequest modifyReviewRequest) {
        Review review = reviewRepository.findReviewBy(modifyReviewRequest.reviewId())
                .orElseThrow(ReviewNotFoundException::new);

        review.modifyReview(modifyReviewRequest.content());

        return ModifyReviewResponse.of(review.getId());
    }

    public void deleteReview(Long reviewId, Long movieId) {
        Review review = reviewRepository.findReviewByMovieIdAndReviewId(reviewId, movieId)
                .orElseThrow(ReviewNotFoundException::new);
        review.delete();
    }
}
