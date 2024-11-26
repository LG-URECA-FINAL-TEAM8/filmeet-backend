package com.ureca.filmeet.domain.genre.repository;

import com.ureca.filmeet.domain.genre.entity.GenreScore;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GenreScoreBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<GenreScore> genreScores) {
        String sql = "INSERT INTO genre_score (member_id, genre_id, score) VALUES (?, ?, ?)";

        List<Object[]> batchArgs = genreScores.stream()
                .map(genreScore -> new Object[]{
                        genreScore.getUser().getId(),
                        genreScore.getGenre().getId(),
                        genreScore.getScore()
                })
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}