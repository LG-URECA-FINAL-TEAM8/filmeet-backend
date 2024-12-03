package com.ureca.filmeet.domain.user.dto.request;

import java.util.List;

public record UpdatePreferenceRequest(
        String mbti,
        Integer age,
        List<Long> genreIds
) {
}
