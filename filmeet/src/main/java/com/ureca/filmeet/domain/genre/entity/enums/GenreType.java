package com.ureca.filmeet.domain.genre.entity.enums;

import lombok.Getter;

@Getter
public enum GenreType {

    ACTION(new String[]{"액션"}),
    ADVENTURE(new String[]{"어드벤처"}),
    ANIMATION(new String[]{"애니"}),
    COMEDY(new String[]{"코미디", "코메디"}),
    CRIME(new String[]{"범죄"}),
    DOCUMENTARY(new String[]{"다큐"}),
    DRAMA(new String[]{"드라마"}),
    ERO(new String[]{"에로"}),
    FAMILY(new String[]{"가족"}),
    FANTASY(new String[]{"판타지"}),
    HISTORICAL(new String[]{"시대극", "사극"}),
    HORROR(new String[]{"호러", "공포"}),
    MUSIC(new String[]{"뮤직"}),
    MUSICAL(new String[]{"뮤지컬"}),
    MYSTERY(new String[]{"미스터리"}),
    NOIR(new String[]{"느와르"}),
    ROMANCE(new String[]{"로맨스", "멜로드라마", "멜로", "로맨틱"}),
    SF(new String[]{"SF"}),
    THRILLER(new String[]{"스릴러"}),
    WAR(new String[]{"전쟁"}),
    UNKNOWN(new String[]{});

    private final String[] names;

    GenreType(String[] names) {
        this.names = names;
    }

    // 주어진 문자열과 매칭되는 Enum 반환
    public static GenreType fromName(String name) {
        for (GenreType genre : values()) {
            for (String genreName : genre.names) {
                if (genreName.equalsIgnoreCase(name)) {
                    return genre;
                }
            }
        }
        return GenreType.UNKNOWN; // UNKNOWN 반환
    }
}
