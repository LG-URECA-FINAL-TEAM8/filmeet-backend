package com.ureca.filmeet.infra.kmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KmdbApiResponse(
        String title,              // 영화 제목
        String titleEng,           // 영어 제목
        String prodYear,           // 제작 연도
        List<KmdbDirector> directors, // 감독 목록
        List<KmdbActor> actors,       // 배우 목록
        String nation,             // 제작 국가
        List<KmdbPlot> plots,         // 줄거리 목록
        String runtime,            // 상영 시간
        String rating,             // 관람 등급
        String genre,              // 장르
        List<String> posters       // 포스터 이미지 URL 목록
) {}
