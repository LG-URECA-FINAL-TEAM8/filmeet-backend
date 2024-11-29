package com.ureca.filmeet.domain.review.exception;

import com.ureca.filmeet.global.exception.ResponseCode;

public class ReviewCommentNotFoundException extends ReviewException {

    public ReviewCommentNotFoundException() {
        super(ResponseCode.REVIEW_COMMENT_NOT_FOUND);
    }
}
