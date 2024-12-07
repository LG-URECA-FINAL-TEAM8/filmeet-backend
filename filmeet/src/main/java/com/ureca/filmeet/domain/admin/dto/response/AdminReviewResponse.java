package com.ureca.filmeet.domain.admin.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record AdminReviewResponse(
        Long id,
        String movieTitle,
        String username,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        String content
) {
    @QueryProjection
    public AdminReviewResponse(Long id, String movieTitle, String username, LocalDateTime createdAt, LocalDateTime lastModifiedAt, String content) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.username = username;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.content = content;
    }
}
