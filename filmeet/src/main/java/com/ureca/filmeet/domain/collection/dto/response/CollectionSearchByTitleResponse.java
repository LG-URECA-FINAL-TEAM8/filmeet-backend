package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.collection.entity.Collection;

public record CollectionSearchByTitleResponse(

        Long collectionId,
        Long userId,
        String title,
        String content,
        String nickname,
        String profileImage
) {

    public static CollectionSearchByTitleResponse of(Collection collection) {
        return new CollectionSearchByTitleResponse(
                collection.getId(),
                collection.getUser().getId(),
                collection.getTitle(),
                collection.getContent(),
                collection.getUser().getNickname(),
                collection.getUser().getProfileImage()
        );
    }
}
