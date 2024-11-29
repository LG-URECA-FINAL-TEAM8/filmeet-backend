package com.ureca.filmeet.domain.review.exception;

import com.ureca.filmeet.global.exception.ResponseCode;

public class ReviewUserNotFoundException extends ReviewException {

    public ReviewUserNotFoundException() {
        super(ResponseCode.REVIEW_USER_NOT_FOUND);
    }
}
