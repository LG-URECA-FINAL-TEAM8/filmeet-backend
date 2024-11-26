package com.ureca.filmeet.domain.review.dto.response;

import com.ureca.filmeet.domain.review.entity.ReviewComment;
import java.time.LocalDateTime;

public record ReviewCommentResponse(

        Long reviewCommentId,
        Long userId,
        String content,
        String nickName,
        String profileImage,
        LocalDateTime createdAt
) {

    public static ReviewCommentResponse of(ReviewComment reviewComment) {
        return new ReviewCommentResponse(
                reviewComment.getId(),
                reviewComment.getUser().getId(),
                reviewComment.getContent(),
                reviewComment.getUser().getNickname(),
                reviewComment.getUser().getProfileImage(),
                reviewComment.getCreatedAt()
        );
    }
}