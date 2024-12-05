package com.ureca.filmeet.domain.admin.dto.response;

import java.time.LocalDateTime;

public record AdminMovieLikesResponse(
        Long id,
        Long movieId,
        String movieTitle,
        Long userId,
        String username,
        LocalDateTime createdAt
) {
}
