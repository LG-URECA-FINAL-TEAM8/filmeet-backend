package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;

public class GameAbandonedException extends GameException {
    public GameAbandonedException() {
        super(ResponseCode.GAME_ABANDONED);
    }
}
