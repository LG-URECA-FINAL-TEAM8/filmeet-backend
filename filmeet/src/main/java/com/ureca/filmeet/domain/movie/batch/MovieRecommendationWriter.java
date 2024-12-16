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
        String updateSql = "UPDATE movie_recommendation SET last_modified_at = CURRENT_TIMESTAMP, movie_id = ? WHERE member_id = ? AND movie_id = ?";
        String insertSql = "INSERT INTO movie_recommendation (member_id, movie_id, created_at, last_modified_at) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        List<Object[]> updateBatchArgs = new ArrayList<>();
        List<Object[]> insertBatchArgs = new ArrayList<>();

        for (List<MovieRecommendation> recommendations : moviesRecommendation) {
            for (MovieRecommendation recommendation : recommendations) {
                Long userId = recommendation.getUser().getId();
                Long movieId = recommendation.getMovie().getId();

                boolean exists = recommendationExists(userId, movieId);

                if (exists) {
                    updateBatchArgs.add(new Object[]{movieId, userId, movieId});
                } else {
                    insertBatchArgs.add(new Object[]{userId, movieId});
                }
            }
        }

        // Execute batch updates and inserts
        if (!updateBatchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(updateSql, updateBatchArgs);
            log.info("Updated {} recommendations.", updateBatchArgs.size());
        }
        if (!insertBatchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, insertBatchArgs);
            log.info("Inserted {} recommendations.", insertBatchArgs.size());
        }
    }

    private boolean recommendationExists(Long userId, Long movieId) {
        String query = "SELECT COUNT(*) FROM movie_recommendation WHERE member_id = ? AND movie_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, userId, movieId);
        return count != null && count > 0;
    }
}
