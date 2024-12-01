package com.ureca.filmeet.domain.collection.dto.request;

public record CollectionCommentModifyRequest(

        Long collectionCommentId,
        String commentContent
) {
}
