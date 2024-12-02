package com.ureca.filmeet.domain.collection.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class CollectionLikeNotFoundException extends CollectionException {

    public CollectionLikeNotFoundException() {
        super(ResponseCode.COLLECTION_LIKE_NOT_FOUND);
    }
}