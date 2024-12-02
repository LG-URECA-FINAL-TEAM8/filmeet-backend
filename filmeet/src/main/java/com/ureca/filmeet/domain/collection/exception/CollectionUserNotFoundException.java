package com.ureca.filmeet.domain.collection.exception;

import com.ureca.filmeet.global.exception.GlobalException;
import com.ureca.filmeet.global.exception.code.ResponseCode;

public class CollectionUserNotFoundException extends GlobalException {

    public CollectionUserNotFoundException() {
        super(ResponseCode.COLLECTION_USER_NOT_FOUND);
    }
}
