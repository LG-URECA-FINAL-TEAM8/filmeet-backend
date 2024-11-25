package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.review.entity.Review;
import java.math.BigDecimal;

public record ReviewInfo(

        BigDecimal star,
        String content
) {
    
    public static ReviewInfo of(Review review) {
        return new ReviewInfo(
                review.getStar(),
                review.getContent()
        );
    }
}
