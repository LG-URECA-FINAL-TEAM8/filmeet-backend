package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.collection.entity.Collection;
import java.util.List;

public record CollectionGetResponse(
        Long collectionId,
        String collectionTitle,
        String collectionContent,
        String userName,
        String userProfileImage,
        List<MovieInfoResponse> movies
) {

    public static CollectionGetResponse from(Collection collection, List<MovieInfoResponse> movies) {
        return new CollectionGetResponse(
                collection.getId(),
                collection.getTitle(),
                collection.getContent(),
                collection.getUser().getUsername(),
                collection.getUser().getProfileImage(),
                movies
        );
    }
}