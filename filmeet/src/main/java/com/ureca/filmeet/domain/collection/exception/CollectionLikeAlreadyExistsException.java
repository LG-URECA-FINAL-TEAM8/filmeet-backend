package com.ureca.filmeet.domain.collection.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class CollectionLikeAlreadyExistsException extends CollectionException {

    public CollectionLikeAlreadyExistsException() {
        super(ResponseCode.COLLECTION_LIKE_ALREADY_EXISTS);
    }
}
