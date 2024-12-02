package com.ureca.filmeet.infra.omdb.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class OmdbOpenApiService {

    private final RestTemplate restTemplate;

    private String omdbApiUrl = "http://www.omdbapi.com";

    @Value("${omdb.apikey}")
    private String apiKey;

    public OmdbApiResponse getMovieByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be null or empty.");
        }

        String url = String.format("%s?apikey=%s&t=%s",
                omdbApiUrl,
                apiKey,
                encodeParam(title)
        );

        OmdbApiResponse response = restTemplate.getForObject(url, OmdbApiResponse.class);
        if (response == null || "False".equalsIgnoreCase(response.response())) {
            throw new RuntimeException("OMDB API returned an error or no results found.");
        }

        return response;
    }

    private String encodeParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}
