package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameAlreadyCompletedException extends GameException {
    public GameAlreadyCompletedException() {
        super(ResponseCode.GAME_ALREADY_COMPLETED);
    }
}
