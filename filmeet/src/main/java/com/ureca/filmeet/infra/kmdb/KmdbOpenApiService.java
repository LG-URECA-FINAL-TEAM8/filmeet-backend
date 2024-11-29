package com.ureca.filmeet.infra.kmdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.filmeet.infra.kmdb.dto.KmdbActor;
import com.ureca.filmeet.infra.kmdb.dto.KmdbApiResponse;
import com.ureca.filmeet.infra.kmdb.dto.KmdbDirector;
import com.ureca.filmeet.infra.kmdb.dto.KmdbPlot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KmdbOpenApiService {

    private final RestTemplate restTemplate;

    private String kmdbApiUrl = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp";

    @Value("${kmdb.apikey}")
    private String serviceKey;

    public KmdbOpenApiService(RestTemplate kmdbRestTemplate) {
        this.restTemplate = kmdbRestTemplate;
    }

    public List<KmdbApiResponse> searchMovies(String director, String query, String actor) {
        // URL 생성
        StringBuilder urlBuilder = new StringBuilder(
                String.format("%s?collection=kmdb_new2&detail=Y&ServiceKey=%s", kmdbApiUrl, serviceKey)
        );
        if (director == null && query == null && actor == null) {
            throw new IllegalArgumentException("하나 이상의 파라미터를 제공해야 합니다.");
        }

        // 조건부 파라미터 추가
        if (director != null) {
            urlBuilder.append("&director=").append(director);
        }
        if (query != null) {
            urlBuilder.append("&query=").append(query);
        }
        if (actor != null) {
            urlBuilder.append("&actor=").append(actor);
        }

        String url = urlBuilder.toString();

        // API 요청
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // API 응답에서 "Data" -> "Result" 부분 추출
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("Data");
        if (dataList == null || dataList.isEmpty()) {
            throw new RuntimeException("KMDB API 응답에 데이터가 없습니다.");
        }

        List<Map<String, Object>> resultList = (List<Map<String, Object>>) dataList.get(0).get("Result");
        if (resultList == null || resultList.isEmpty()) {
            throw new RuntimeException("KMDB API 응답에 유효한 Result가 없습니다.");
        }

        // Result 데이터를 DTO로 변환
        return resultList.stream()
                .map(this::mapToKmdbApiResponse)
                .toList();
    }

    private KmdbApiResponse mapToKmdbApiResponse(Map<String, Object> map) {
        return new KmdbApiResponse(
                cleanResponseString((String) map.get("title")),              // 제목
                (String) map.get("titleEng"),                               // 영어 제목
                (String) map.get("prodYear"),                               // 제작 연도
                cleanDirectors(map.get("directors")),                       // 감독
                cleanActors(map.get("actors")),                             // 배우
                (String) map.get("nation"),                                 // 제작 국가
                mapToList(map.get("plots"), "plot", KmdbPlot.class),        // 줄거리
                (String) map.get("runtime"),                                // 상영 시간
                (String) map.get("rating"),                                 // 관람 등급
                (String) map.get("genre"),                                  // 장르
                mapToStringList(map.get("posters"))                         // 포스터 이미지
        );
    }


    private <T> List<T> mapToList(Object obj, String key, Class<T> type) {
        if (obj instanceof Map<?, ?> map) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(key);
            return list == null ? List.of() : list.stream()
                    .map(item -> {
                        Map<String, Object> cleanedMap = item.entrySet().stream()
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> entry.getValue() instanceof String str ? cleanResponseString(str) : entry.getValue()
                                ));
                        return mapObjectToDto(cleanedMap, type);
                    })
                    .toList();
        }
        return List.of();
    }


    private <T> T mapObjectToDto(Map<String, Object> map, Class<T> type) {
        // Spring의 ObjectMapper를 활용한 변환
        return new ObjectMapper().convertValue(map, type);
    }

    private List<String> mapToStringList(Object obj) {
        if (obj instanceof String str) {
            return List.of(str.split("\\|"));
        }
        return List.of();
    }

    private String cleanResponseString(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("!HS|!HE", "").trim();
    }

    private List<KmdbDirector> cleanDirectors(Object directors) {
        return mapToList(directors, "director", KmdbDirector.class).stream()
                .map(director -> new KmdbDirector(
                        cleanResponseString(director.directorNm()),
                        director.directorEnNm()
                ))
                .toList();
    }

    private List<KmdbActor> cleanActors(Object actors) {
        return mapToList(actors, "actor", KmdbActor.class).stream()
                .map(actor -> new KmdbActor(
                        cleanResponseString(actor.actorNm()),
                        actor.actorEnNm()
                ))
                .toList();
    }
}
