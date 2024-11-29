package com.ureca.filmeet.domain.review.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class ReviewLikeNotFoundException extends ReviewException {

    public ReviewLikeNotFoundException() {
        super(ResponseCode.REVIEW_LIKE_NOT_FOUND);
    }
}
