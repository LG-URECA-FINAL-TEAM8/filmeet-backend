package com.ureca.filmeet.domain.review.service.query;

import com.ureca.filmeet.domain.review.dto.response.GetMovieReviewsResponse;
import com.ureca.filmeet.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

    public Slice<GetMovieReviewsResponse> getMovieReviews(Long movieId, Long userId, Pageable pageable) {

        return reviewRepository.findMovieReviewsWithLikes(movieId, userId, pageable);
    }
}
