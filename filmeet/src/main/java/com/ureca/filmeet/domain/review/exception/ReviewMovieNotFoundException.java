package com.ureca.filmeet.domain.review.exception;

import com.ureca.filmeet.global.exception.ResponseCode;

public class ReviewMovieNotFoundException extends ReviewException {

    public ReviewMovieNotFoundException() {
        super(ResponseCode.REVIEW_MOVIE_NOT_FOUND);
    }
}
