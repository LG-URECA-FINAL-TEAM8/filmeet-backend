package com.ureca.filmeet.domain.review.exception;

import com.ureca.filmeet.global.exception.ResponseCode;

public class ReviewLikeAlreadyExistsException extends ReviewException {

    public ReviewLikeAlreadyExistsException() {
        super(ResponseCode.REVIEW_LIKE_ALREADY_EXISTS);
    }
}
