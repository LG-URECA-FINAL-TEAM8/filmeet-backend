package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameAlreadyHasWinnerException extends GameException {
    public GameAlreadyHasWinnerException() {
        super(ResponseCode.GAME_ALREADY_HAS_WINNER);
    }
}
