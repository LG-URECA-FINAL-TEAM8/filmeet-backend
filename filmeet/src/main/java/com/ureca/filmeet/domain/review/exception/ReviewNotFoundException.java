package com.ureca.filmeet.domain.review.exception;

import com.ureca.filmeet.global.exception.ResponseCode;

public class ReviewNotFoundException extends ReviewException {

    public ReviewNotFoundException() {
        super(ResponseCode.REVIEW_NOT_FOUND);
    }
}
