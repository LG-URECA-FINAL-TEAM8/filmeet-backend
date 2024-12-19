package com.ureca.filmeet.domain.movie.batch;

import com.ureca.filmeet.domain.movie.entity.MovieRecommendation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
        String insertSql = "INSERT INTO movie_recommendation (member_id, movie_id, created_at, last_modified_at) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        String deleteSql = "DELETE FROM movie_recommendation WHERE member_id IN (%s)";

        List<Object[]> insertBatchArgs = new ArrayList<>();
        Set<Long> deleteMemberIds = new HashSet<>();

        for (List<MovieRecommendation> recommendations : moviesRecommendation) {
            for (MovieRecommendation recommendation : recommendations) {
                Long userId = recommendation.getUser().getId();
                Long movieId = recommendation.getMovie().getId();

                deleteMemberIds.add(userId);
                insertBatchArgs.add(new Object[]{userId, movieId});
            }
        }

        if (!deleteMemberIds.isEmpty()) {
            String inClause = deleteMemberIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            jdbcTemplate.update(String.format(deleteSql, inClause));
            log.info("Deleted recommendations for {} members.", deleteMemberIds.size());
        }

        if (!insertBatchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, insertBatchArgs);
            log.info("Inserted {} recommendations.", insertBatchArgs.size());
        }
    }
}

