package com.ureca.filmeet.domain.review.service.command;

import com.ureca.filmeet.domain.movie.entity.Movie;
import com.ureca.filmeet.domain.movie.repository.MovieRepository;
import com.ureca.filmeet.domain.review.dto.request.CreateReviewRequest;
import com.ureca.filmeet.domain.review.dto.request.ModifyReviewRequest;
import com.ureca.filmeet.domain.review.dto.response.CreateReviewResponse;
import com.ureca.filmeet.domain.review.dto.response.ModifyReviewResponse;
import com.ureca.filmeet.domain.review.entity.Review;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import com.ureca.filmeet.domain.user.entity.User;
import com.ureca.filmeet.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    public CreateReviewResponse createReview(CreateReviewRequest createReviewRequest) {
        User user = userRepository.findById(createReviewRequest.userId())
                .orElseThrow(() -> new RuntimeException("no user"));

        Movie movie = movieRepository.findById(createReviewRequest.movieId())
                .orElseThrow(() -> new RuntimeException("no movie"));

        Review review = Review.builder()
                .content(createReviewRequest.content())
                .movie(movie)
                .user(user)
                .build();

        movie.addReviewCounts();

        Review saveReview = reviewRepository.save(review);
        return CreateReviewResponse.of(saveReview.getId());
    }

    public ModifyReviewResponse modifyReview(ModifyReviewRequest modifyReviewRequest) {
        Review review = reviewRepository.findReviewBy(modifyReviewRequest.reviewId())
                .orElseThrow(() -> new RuntimeException("no review"));

        review.modifyReview(modifyReviewRequest.content());

        return ModifyReviewResponse.of(review.getId());
    }

    public void deleteReview(Long reviewId, Long movieId) {
        Review review = reviewRepository.findReviewByMovieIdAndReviewId(reviewId, movieId)
                .orElseThrow(() -> new RuntimeException("no review"));
        review.delete();

        Movie movie = movieRepository.findMovieByReviewIdAndMovieId(reviewId, movieId)
                .orElseThrow(() -> new RuntimeException("no movie"));
        movie.decrementReviewCounts();
    }
}
