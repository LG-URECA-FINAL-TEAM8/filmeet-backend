package com.ureca.filmeet.domain.movie.batch;

import com.ureca.filmeet.domain.movie.entity.MovieRecommendation;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieRecommendationWriter implements ItemWriter<List<MovieRecommendation>> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends List<MovieRecommendation>> moviesRecommendation) {
        String deleteSql = "DELETE FROM movie_recommendation WHERE member_id = ? AND movie_id = ?";
        String insertSql = "INSERT INTO movie_recommendation (member_id, movie_id, created_at, last_modified_at) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        List<Object[]> deleteBatchArgs = new ArrayList<>();
        List<Object[]> insertBatchArgs = new ArrayList<>();

        for (List<MovieRecommendation> recommendations : moviesRecommendation) {
            for (MovieRecommendation recommendation : recommendations) {
                Long userId = recommendation.getUser().getId();
                Long movieId = recommendation.getMovie().getId();

                deleteBatchArgs.add(new Object[]{userId, movieId});
                insertBatchArgs.add(new Object[]{userId, movieId});
            }
        }

        if (!deleteBatchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(deleteSql, deleteBatchArgs);
            log.info("Deleted {} recommendations.", deleteBatchArgs.size());
        }

        if (!insertBatchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, insertBatchArgs);
            log.info("Inserted {} recommendations.", insertBatchArgs.size());
        }
    }
}
