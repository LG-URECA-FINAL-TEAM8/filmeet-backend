package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.review.entity.Review;

public record MyMovieReview(

        String content
) {

    public static MyMovieReview of(Review review) {
        return new MyMovieReview(
                review.getContent()
        );
    }
}
