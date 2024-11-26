package com.ureca.filmeet.domain.review.dto.response;

import com.ureca.filmeet.domain.review.entity.Review;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record GetMovieReviewDetailResponse(

        Long reviewId,
        Long userId,
        Long movieId,
        String content,
        int likeCounts,
        int commentCounts,
        BigDecimal star,
        LocalDateTime createdAt,
        String nickName,
        String profileImage,
        String movieTitle,
        String posterUrl,
        LocalDate movieReleaseDate,
        Boolean isLiked,
        List<ReviewCommentResponse> reviewCommentResponses
) {

    public static GetMovieReviewDetailResponse from(Review review, List<ReviewCommentResponse> reviewCommentResponses,
                                                    boolean existsReviewLikes) {
        return new GetMovieReviewDetailResponse(
                review.getId(),
                review.getUser().getId(),
                review.getMovie().getId(),
                review.getContent(),
                review.getLikeCounts(),
                review.getCommentCounts(),
                review.getStar(),
                review.getCreatedAt(),
                review.getUser().getNickname(),
                review.getUser().getProfileImage(),
                review.getMovie().getTitle(),
                review.getMovie().getPosterUrl(),
                review.getMovie().getReleaseDate(),
                existsReviewLikes,
                reviewCommentResponses
        );
    }
}
