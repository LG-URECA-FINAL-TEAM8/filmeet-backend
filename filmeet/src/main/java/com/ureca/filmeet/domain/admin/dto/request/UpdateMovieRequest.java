package com.ureca.filmeet.domain.admin.dto.request;

public record UpdateMovieRequest(
        String title,
        String posterUrl,
        Integer likeCounts
) {
}
