package com.ureca.filmeet.infra.kmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KmdbActor(
        String actorNm,   // 배우 이름
        String actorEnNm, // 배우 영어 이름
        String actorId
) {}
