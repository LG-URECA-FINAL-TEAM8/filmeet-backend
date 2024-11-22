package com.ureca.filmeet.domain.movie.dto.response;

import com.ureca.filmeet.domain.movie.entity.enums.MoviePosition;

public record PersonnelInfoResponse(

        MoviePosition moviePosition,
        String characterName,
        String name,
        String profileImage
) {
}