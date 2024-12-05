package com.ureca.filmeet.domain.user.entity;

import lombok.Getter;

@Getter
public enum Permission {
    COMMON_READ("COMMON_READ_AUTHORITY"),
    COMMON_CREATE("COMMON_CREATE_AUTHORITY"),
    COMMON_UPDATE("COMMON_UPDATE_AUTHORITY"),
    COMMON_DELETE("COMMON_DELETE_AUTHORITY"),

    //movie
    MOVIE_CREATE("MOVIE_CREATE_AUTHORITY"),
    MOVIE_UPDATE("MOVIE_UPDATE_AUTHORITY"),
    MOVIE_DELETE("MOVIE_DELETE_AUTHORITY");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }
}