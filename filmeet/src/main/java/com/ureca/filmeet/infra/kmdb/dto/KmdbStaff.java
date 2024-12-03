package com.ureca.filmeet.infra.kmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KmdbStaff(
        String staffId,
        String staffRoleGroup,
        String staffRole,
        String staffNm
) {
}
