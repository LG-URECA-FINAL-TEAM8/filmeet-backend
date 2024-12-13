package com.ureca.filmeet.infra.kmdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.filmeet.infra.kmdb.dto.KmdbApiResponse;
import com.ureca.filmeet.infra.kmdb.dto.KmdbPlot;
import com.ureca.filmeet.infra.kmdb.dto.KmdbStaff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
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
                String.format("%s?collection=kmdb_new2&detail=Y&ServiceKey=%s&listCount=20", kmdbApiUrl, serviceKey)
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
                (String) map.get("repRlsDate"),                               // 제작 연도
                cleanStaffs(map.get("staffs")),
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
        return input.replaceAll("!HS|!HE|", "")
                .trim()
                .replaceAll("\\s+", " "); // 중첩된 공백 하나로 변환
    }


    private List<KmdbStaff> cleanStaffs(Object staffs) {
        return mapToList(staffs, "staff", KmdbStaff.class).stream()
                .filter(staff -> staff.staffId() != null && !staff.staffId().isBlank()) // staffId가 없는 경우 제외
                .filter(staff -> {
                    // 조건 1: 감독, 출연, 각본만 허용
                    if (!"감독".equals(staff.staffRoleGroup()) && !"출연".equals(staff.staffRoleGroup()) && !"각본".equals(staff.staffRoleGroup())) {
                        return false;
                    }
                    // 조건 2: 출연인 경우 staffRole이 반드시 있어야 함
                    if ("출연".equals(staff.staffRoleGroup()) && (staff.staffRole() == null || staff.staffRole().isBlank())) {
                        return false;
                    }
                    return true;
                })
                .map(staff -> new KmdbStaff(
                        staff.staffId(),
                        staff.staffRoleGroup(),
                        staff.staffRole(),
                        cleanResponseString(staff.staffNm())
                ))
                .toList();
    }
}
