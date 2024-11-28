package com.ureca.filmeet.domain.review.entity.enums;

import lombok.Getter;

@Getter
public enum PopularityWeight {
    LIKE(2.0),
    COMMENT(1.5),
    TIME_DECAY(0.1),
    BASE_SCORE(5.0),
    MAX_DAYS(60);

    private final double weight;

    PopularityWeight(double weight) {
        this.weight = weight;
    }
}