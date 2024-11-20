package com.ureca.filmeet.domain.collection.dto.request;

import java.util.List;

public record CollectionModifyRequest(
        Long collectionId,
        String title,
        String content,
        List<Long> movieIds
) {
}
