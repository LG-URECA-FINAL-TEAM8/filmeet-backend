package com.ureca.filmeet.infra.kobis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KobisOpenAPIRestService {

    @Value("${kobis.apikey}")
    private String apiKey;

    private static final int ITEM_PER_PAGE = 10;
    private static final String BASE_URL = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Kobis Open API에서 일별 박스오피스 데이터를 가져옵니다.
     *
     * @return 박스오피스 결과 리스트.
     */
    public List<Map<String, String>> fetchDailyBoxOffice() {
        String targetDate = calculateTargetDate();
        String uri = buildUri(targetDate);

        log.debug("Fetching daily box office data from URI: {}", uri);

        try {
            // HTTP 요청 및 응답 처리
            String responseBody = sendHttpRequest(uri);

            // JSON 데이터 파싱
            List<Map<String, String>> boxOfficeResults = parseBoxOfficeData(responseBody);

            log.info("Successfully fetched {} box office entries for date: {}", boxOfficeResults.size(), targetDate);
            return boxOfficeResults;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch or parse API response", e);
        }
    }

    /**
     * 대상 날짜(어제)를 계산합니다.
     *
     * @return 포맷팅된 날짜 문자열.
     */
    private String calculateTargetDate() {
        return LocalDate.now().minusDays(1).format(DATE_FORMAT);
    }

    /**
     * API 요청을 위한 URI를 생성합니다.
     *
     * @param targetDate 박스오피스 데이터를 위한 대상 날짜.
     * @return 완성된 URI 문자열.
     */
    private String buildUri(String targetDate) {
        return String.format("%s?key=%s&targetDt=%s&itemPerPage=%d", BASE_URL, apiKey, targetDate, ITEM_PER_PAGE);
    }

    /**
     * 지정된 URI로 HTTP 요청을 보내고 응답 본문을 반환합니다.
     *
     * @param uri 요청할 URI.
     * @return 응답 본문 문자열.
     * @throws Exception HTTP 요청 중 오류가 발생한 경우.
     */
    private String sendHttpRequest(String uri) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch data: HTTP Status Code " + response.statusCode());
        }

        return response.body();
    }

    /**
     * JSON 응답 본문을 파싱하여 박스오피스 데이터를 추출합니다.
     *
     * @param responseBody JSON 응답 본문.
     * @return 박스오피스 결과 리스트.
     * @throws Exception JSON 파싱 중 오류가 발생한 경우.
     */
    private List<Map<String, String>> parseBoxOfficeData(String responseBody) throws Exception {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode boxOfficeList = rootNode.path("boxOfficeResult").path("dailyBoxOfficeList");

        List<Map<String, String>> boxOfficeResults = new ArrayList<>();
        for (JsonNode boxOffice : boxOfficeList) {
            Map<String, String> movieInfo = new HashMap<>();
            movieInfo.put("rank", boxOffice.path("rnum").asText());
            movieInfo.put("movieName", boxOffice.path("movieNm").asText().replaceAll("\\s+", ""));
            movieInfo.put("releaseDate", boxOffice.path("openDt").asText());
            movieInfo.put("dailyAudience", boxOffice.path("audiCnt").asText());
            movieInfo.put("totalAudience", boxOffice.path("audiAcc").asText());
            boxOfficeResults.add(movieInfo);
        }

        return boxOfficeResults;
    }
}