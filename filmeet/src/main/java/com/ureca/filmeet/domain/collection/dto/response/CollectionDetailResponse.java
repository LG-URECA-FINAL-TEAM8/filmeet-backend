package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.collection.entity.Collection;

public record CollectionDetailResponse(
        Long collectionId,
        String collectionTitle,
        String collectionContent,
        String nickname,
        String userProfileImage,
        Integer likeCounts,
        Integer commentCounts
) {

    public static CollectionDetailResponse of(Collection collection) {
        return new CollectionDetailResponse(
                collection.getId(),
                collection.getTitle(),
                collection.getContent(),
                collection.getUser().getNickname(),
                collection.getUser().getProfileImage(),
                collection.getLikeCounts(),
                collection.getCommentCounts()
        );
    }
}