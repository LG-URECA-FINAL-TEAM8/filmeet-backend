package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;


public class GameRoundsEmptyException extends GameValidationException {
    public GameRoundsEmptyException() {
        super(ResponseCode.GAME_ROUNDS_EMPTY);
    }
}
