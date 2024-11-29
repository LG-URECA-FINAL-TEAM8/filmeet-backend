package com.ureca.filmeet.infra.kmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KmdbPlot(
        String plotLang, // 줄거리 언어
        String plotText  // 줄거리 내용
) {}

