package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionMovieRepository extends JpaRepository<CollectionMovie, Long> {

    @Query("SELECT cm.movie.id " +
            "FROM CollectionMovie cm " +
            "WHERE cm.collection.id = :collectionId")
    List<Long> findMovieIdsByCollectionId(@Param("collectionId") Long collectionId);

    @Query("SELECT cm " +
            "FROM CollectionMovie cm " +
            "JOIN FETCH cm.movie m " +
            "WHERE cm.collection.id IN :collectionIds "
    )
    List<CollectionMovie> findMoviesByCollectionIds(@Param("collectionIds") List<Long> collectionIds);

    @Query(""" 
            SELECT cm
            FROM CollectionMovie cm
            JOIN FETCH cm.movie m
            WHERE cm.collection.id = :collectionId
            """)
    Slice<CollectionMovie> findMoviesBy(
            @Param("collectionId") Long collectionId,
            Pageable pageable
    );

    @Modifying
    @Query("DELETE FROM CollectionMovie cm " +
            "WHERE cm.collection.id = :collectionId AND cm.movie.id IN :movieIds")
    void deleteByCollectionIdAndMovieIds(@Param("collectionId") Long collectionId,
                                         @Param("movieIds") List<Long> movieIds);
}
