package com.ureca.filmeet.domain.collection.exception;

import com.ureca.filmeet.domain.movie.exception.MovieException;
import com.ureca.filmeet.global.exception.code.ResponseCode;

public class CollectionCommentNotFoundException extends MovieException {

    public CollectionCommentNotFoundException() {
        super(ResponseCode.COLLECTION_COMMENT_NOT_FOUND);
    }
}
