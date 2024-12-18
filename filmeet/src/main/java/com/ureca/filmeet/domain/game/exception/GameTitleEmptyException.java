package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameTitleEmptyException extends GameValidationException {
    public GameTitleEmptyException() {
        super(ResponseCode.GAME_TITLE_EMPTY);
    }
}
