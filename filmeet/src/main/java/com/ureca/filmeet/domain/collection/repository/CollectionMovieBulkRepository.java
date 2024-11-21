package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.Collection;
import com.ureca.filmeet.domain.movie.entity.Movie;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CollectionMovieBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(Collection collection, List<Movie> movies) {
        String sql = "INSERT INTO collection_movie (collection_id, movie_id) VALUES (?, ?)";

        List<Object[]> batchArgs = movies.stream()
                .map(movie -> new Object[]{collection.getId(), movie.getId()})
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}