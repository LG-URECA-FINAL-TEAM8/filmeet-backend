package com.ureca.filmeet.domain.game.exception;

import com.ureca.filmeet.global.exception.code.ResponseCode;


public class GameSelectedMovieEmptyException extends GameValidationException {
    public GameSelectedMovieEmptyException() {
        super(ResponseCode.GAME_SELECTED_MOVIE_EMPTY);
    }
}