package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

// GameMatchNotFoundException
public class GameInvalidRoundException extends GameException {
    public GameInvalidRoundException() {
        super(ResponseCode.GAME_MATCH_NOT_FOUND);
    }
}
