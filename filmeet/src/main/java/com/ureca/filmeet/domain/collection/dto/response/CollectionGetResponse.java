package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.collection.entity.Collection;
import java.util.List;

public record CollectionGetResponse(
        Long collectionId,
        String collectionTitle,
        String collectionContent,
        String nickname,
        String userProfileImage,
        Integer likeCounts,
        Integer commentCounts,
        List<MovieInfoResponse> movies
) {

    public static CollectionGetResponse from(Collection collection, List<MovieInfoResponse> movies) {
        return new CollectionGetResponse(
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