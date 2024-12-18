package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameMatchNotFoundException extends GameException {
    public GameMatchNotFoundException() {
        super(ResponseCode.GAME_MATCH_NOT_FOUND);
    }
}
