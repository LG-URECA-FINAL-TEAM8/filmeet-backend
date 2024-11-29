package com.ureca.filmeet.infra.kmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KmdbDirector(
        String directorNm,   // 감독 이름
        String directorEnNm // 감독 영어 이름
) {}
