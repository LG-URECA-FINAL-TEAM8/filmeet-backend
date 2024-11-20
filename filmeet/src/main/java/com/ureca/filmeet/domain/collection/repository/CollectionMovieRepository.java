package com.ureca.filmeet.domain.collection.repository;

import com.ureca.filmeet.domain.collection.entity.CollectionMovie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionMovieRepository extends JpaRepository<CollectionMovie, Long> {
}
