package com.ureca.filmeet.domain.movie.repository;

import com.ureca.filmeet.infra.kobis.KobisOpenAPIRestService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoxOfficeCacheStore {

    private final CopyOnWriteArrayList<Map<String, String>> cachedBoxOfficeMovies = new CopyOnWriteArrayList<>();

    private final KobisOpenAPIRestService kobisOpenAPIRestService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void updateBoxOfficeMovies() {
        try {
            List<Map<String, String>> boxOfficeMovies = fetchBoxOfficeMovies();
            cachedBoxOfficeMovies.clear();
            cachedBoxOfficeMovies.addAll(boxOfficeMovies);
            log.info("Successfully updated box office data in cache.");
        } catch (Exception e) {
            log.error("Failed to update box office data", e);
        }
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<Map<String, String>> fetchBoxOfficeMovies() {
        try {
            log.info("Fetching box office data...");
            return kobisOpenAPIRestService.fetchDailyBoxOffice();
        } catch (RuntimeException e) {
            log.error("Error fetching box office data, retrying...", e);
            throw e; // 재시도 대상 예외 던지기
        }
    }

    public List<Map<String, String>> getBoxOfficeMovies() {
        if (cachedBoxOfficeMovies.isEmpty()) {
            log.warn("Box office cache is empty.");
            return List.of();
        }
        return new ArrayList<>(cachedBoxOfficeMovies);
    }
}