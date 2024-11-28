package com.ureca.filmeet.domain.collection.dto.request;

public record CollectionCommentCreateRequest(

        Long collectionId,
        String commentContent
) {
}
