package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameResultNotFoundException extends GameException {
    public GameResultNotFoundException() {
        super(ResponseCode.GAME_RESULT_NOT_FOUND);
    }
}
