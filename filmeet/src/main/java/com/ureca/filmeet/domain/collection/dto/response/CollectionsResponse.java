package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.collection.entity.Collection;
import java.util.List;

public record CollectionsResponse(
        Long collectionId,
        String collectionTitle,
        String collectionContent,
        String nickname,
        String userProfileImage,
        Integer likeCounts,
        Integer commentCounts,
        List<CollectionMovieInfoResponse> movies
) {

    public static CollectionsResponse from(Collection collection, List<CollectionMovieInfoResponse> movies) {
        return new CollectionsResponse(
                collection.getId(),
                collection.getTitle(),
                collection.getContent(),
                collection.getUser().getNickname(),
                collection.getUser().getProfileImage(),
                collection.getLikeCounts(),
                collection.getCommentCounts(),
                movies
        );
    }
}