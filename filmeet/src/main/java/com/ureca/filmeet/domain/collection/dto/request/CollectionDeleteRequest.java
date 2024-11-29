package com.ureca.filmeet.domain.collection.dto.request;

import java.util.List;

public record CollectionDeleteRequest(

        Long collectionId,
        List<Long> movieIds
) {
}
