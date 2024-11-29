package com.ureca.filmeet.domain.review.exception;

import com.ureca.filmeet.global.exception.ResponseCode;

public class ReviewAlreadyExistsException extends ReviewException {

    public ReviewAlreadyExistsException() {
        super(ResponseCode.REVIEW_ALREADY_EXISTS);
    }
}
