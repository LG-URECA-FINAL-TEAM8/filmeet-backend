package com.ureca.filmeet.domain.collection.dto.request;

import java.util.List;

public record CollectionCreateRequest(
        String title,
        String content,
        Long userId,
        List<Long> movieIds
) {
}