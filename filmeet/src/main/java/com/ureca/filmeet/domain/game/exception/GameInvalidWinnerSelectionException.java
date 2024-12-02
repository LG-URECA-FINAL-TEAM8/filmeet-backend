package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameInvalidWinnerSelectionException extends GameException {
    public GameInvalidWinnerSelectionException() {
        super(ResponseCode.GAME_INVALID_WINNER_SELECTION);
    }
}