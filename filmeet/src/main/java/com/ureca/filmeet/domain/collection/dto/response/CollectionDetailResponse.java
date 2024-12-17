package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.collection.entity.Collection;

public record CollectionDetailResponse(
        Long collectionId,
        String collectionTitle,
        String collectionContent,
        String nickname,
        Long userId,
        String userProfileImage,
        Integer likeCounts,
        Integer commentCounts,
        Boolean isLiked
) {

    public static CollectionDetailResponse from(Collection collection, Boolean existsCollectionLike) {
        return new CollectionDetailResponse(
                collection.getId(),
                collection.getTitle(),
                collection.getContent(),
                collection.getUser().getNickname(),
                collection.getUser().getId(),
                collection.getUser().getProfileImage(),
                collection.getLikeCounts(),
                collection.getCommentCounts(),
                existsCollectionLike
        );
    }
}