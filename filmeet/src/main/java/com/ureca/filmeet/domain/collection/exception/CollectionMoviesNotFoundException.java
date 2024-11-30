package com.ureca.filmeet.domain.collection.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class CollectionMoviesNotFoundException extends CollectionException {

    public CollectionMoviesNotFoundException() {
        super(ResponseCode.COLLECTION_MOVIES_NOT_FOUND);
    }
}
