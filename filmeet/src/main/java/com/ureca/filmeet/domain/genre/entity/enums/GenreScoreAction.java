package com.ureca.filmeet.domain.genre.entity.enums;

import lombok.Getter;

@Getter
public enum GenreScoreAction {

    PREFERRED_GENRE(20),
    LIKE(2),
    STAR_RATING(3),
    COLLECTION(4),
    GAME_RESULT(3);

    private final int weight;

    GenreScoreAction(int weight) {
        this.weight = weight;
    }
}
