package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameNotFoundException extends GameException {
    public GameNotFoundException() {
        super(ResponseCode.GAME_NOT_FOUND);
    }
}
