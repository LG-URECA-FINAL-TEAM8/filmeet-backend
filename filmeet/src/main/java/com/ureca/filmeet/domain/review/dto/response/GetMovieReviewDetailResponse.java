package com.ureca.filmeet.domain.review.dto.response;

import com.ureca.filmeet.domain.review.entity.Review;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetMovieReviewDetailResponse(

        Long reviewId,
        Long userId,
        Long movieId,
        String content,
        int likeCounts,
        int commentCounts,
        LocalDateTime createdAt,
        String nickName,
        String profileImage,
        String movieTitle,
        String posterUrl,
        LocalDate movieReleaseDate,
        Boolean isLiked
) {

    public static GetMovieReviewDetailResponse from(
            Review review,
            boolean existsReviewLikes
    ) {
        return new GetMovieReviewDetailResponse(
                review.getId(),
                review.getUser().getId(),
                review.getMovie().getId(),
                review.getContent(),
                review.getLikeCounts(),
                review.getCommentCounts(),
                review.getCreatedAt(),
                review.getUser().getNickname(),
                review.getUser().getProfileImage(),
                review.getMovie().getTitle(),
                review.getMovie().getPosterUrl(),
                review.getMovie().getReleaseDate(),
                existsReviewLikes
        );
    }
}
