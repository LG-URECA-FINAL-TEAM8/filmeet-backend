package com.ureca.filmeet.domain.collection.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class CollectionCommentNotFoundException extends CollectionException {

    public CollectionCommentNotFoundException() {
        super(ResponseCode.COLLECTION_COMMENT_NOT_FOUND);
    }
}
