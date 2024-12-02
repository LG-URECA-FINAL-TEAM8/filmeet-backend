package com.ureca.filmeet.infra.omdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OmdbApiResponse(
        @JsonProperty("Title") String title,        // 영화 제목
        @JsonProperty("Year") String year,         // 제작 연도
        @JsonProperty("Rated") String rated,
        @JsonProperty("Released") String released, // 개봉일
        @JsonProperty("Runtime") String runtime,   // 상영 시간
        @JsonProperty("Genre") String genre,       // 장르
        @JsonProperty("Director") String director, // 감독
        @JsonProperty("Writer") String writer,     // 작가
        @JsonProperty("Actors") String actors,     // 배우
        @JsonProperty("Plot") String plot,         // 줄거리
        @JsonProperty("Language") String language, // 언어
        @JsonProperty("Country") String country,   // 제작 국가
        @JsonProperty("Awards") String awards,     // 수상 내역
        @JsonProperty("Poster") String poster,     // 포스터 URL
        @JsonProperty("Metascore") String metascore, // 메타크리틱 점수
        @JsonProperty("imdbRating") String imdbRating, // IMDb 평점
        @JsonProperty("imdbVotes") String imdbVotes,   // IMDb 투표 수
        @JsonProperty("imdbID") String imdbID,         // IMDb ID
        @JsonProperty("Type") String type,             // 유형 (영화, 시리즈 등)
        @JsonProperty("DVD") String dvd,               // DVD 출시일
        @JsonProperty("BoxOffice") String boxOffice,   // 박스오피스 수익
        @JsonProperty("Production") String production, // 제작사
        @JsonProperty("Website") String website,       // 웹사이트 URL
        @JsonProperty("Response") String response      // API 응답 상태

) {}