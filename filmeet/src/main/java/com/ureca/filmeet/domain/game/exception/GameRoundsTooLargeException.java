package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;


public class GameRoundsTooLargeException extends GameValidationException {
    public GameRoundsTooLargeException() {
        super(ResponseCode.GAME_ROUNDS_TOO_LARGE);
    }
}
