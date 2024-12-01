package com.ureca.filmeet.domain.collection.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class CollectionNotFoundException extends CollectionException {

    public CollectionNotFoundException() {
        super(ResponseCode.COLLECTION_NOT_FOUND);
    }
}
