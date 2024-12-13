package com.ureca.filmeet.domain.collection.dto.response;

import com.ureca.filmeet.domain.collection.entity.CollectionComment;
import java.time.LocalDateTime;

public record CollectionCommentsResponse(

        Long collectionCommentId,
        String commentContent,
        String nickname,
        String profileImage,
        LocalDateTime createdAt
) {

    public static CollectionCommentsResponse of(CollectionComment collectionComment) {
        return new CollectionCommentsResponse(
                collectionComment.getId(),
                collectionComment.getContent(),
                collectionComment.getUser().getNickname(),
                collectionComment.getUser().getProfileImage(),
                collectionComment.getCreatedAt()
        );
    }
}
