package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameNotOwnerException extends GameException {
    public GameNotOwnerException() {
        super(ResponseCode.GAME_NOT_OWNER);
    }
}

