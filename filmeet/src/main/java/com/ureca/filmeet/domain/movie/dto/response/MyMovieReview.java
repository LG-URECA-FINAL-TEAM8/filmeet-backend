package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.review.entity.Review;

public record MyMovieReview(

        Long reviewId,
        String content,
        String userProfileImage
) {
    public static MyMovieReview of(Review review) {
        return new MyMovieReview(
                review.getId(),
                review.getContent(),
                review.getUser().getProfileImage()
        );
    }
}