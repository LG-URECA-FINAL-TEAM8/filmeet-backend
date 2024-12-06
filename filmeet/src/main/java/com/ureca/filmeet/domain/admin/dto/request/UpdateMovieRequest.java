package com.ureca.filmeet.domain.admin.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record UpdateMovieRequest(
        String title,
        String posterUrl,
        Integer likeCounts,
        BigDecimal averageRating,
        List<String> galleries
) {
}
