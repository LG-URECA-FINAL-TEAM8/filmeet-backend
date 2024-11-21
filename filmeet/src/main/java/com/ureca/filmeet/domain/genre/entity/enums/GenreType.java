package com.ureca.filmeet.domain.genre.entity.enums;

import lombok.Getter;

@Getter
public enum GenreType {

    ACTION("액션"),
    HORROR("호러"),
    COMEDY("코미디"),
    DRAMA("드라마"),
    THRILLER("스릴러"),
    ROMANCE("로맨스"),
    CRIME("범죄"),
    FANTASY("판타지"),
    MYSTERY("미스터리"),
    ANIMATION("애니"),
    SF("SF"),
    ADVENTURE("모험"),
    DOCUMENTARY("다큐"),
    MUSICAL("뮤지컬"),
    FAMILY("가족"),
    NOIR("느와르");

    private final String name;

    GenreType(String name) {
        this.name = name;
    }
}