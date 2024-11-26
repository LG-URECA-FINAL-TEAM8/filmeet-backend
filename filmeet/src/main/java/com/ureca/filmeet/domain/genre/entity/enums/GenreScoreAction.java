package com.ureca.filmeet.domain.genre.entity.enums;

import lombok.Getter;

@Getter
public enum GenreScoreAction {

    PREFERRED_GENRE(20),
    GAME_RESULT(3),

    LIKE(2),
    COLLECTION(4),

    LIKE_CANCEL(-2),
    COLLECTION_DELETE(-4);

    private final int weight;

    GenreScoreAction(int weight) {
        this.weight = weight;
    }
}
