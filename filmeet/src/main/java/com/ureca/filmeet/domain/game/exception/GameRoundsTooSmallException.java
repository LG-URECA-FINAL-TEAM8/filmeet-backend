package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;


public class GameRoundsTooSmallException extends GameValidationException {
    public GameRoundsTooSmallException() {
        super(ResponseCode.GAME_ROUNDS_TOO_SMALL);
    }
}

